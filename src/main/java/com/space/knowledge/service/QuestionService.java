package com.space.knowledge.service;

import com.space.knowledge.entity.Question;
import com.space.knowledge.entity.QuestionOption;
import com.space.knowledge.mapper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 题目服务类，提供题目的CRUD操作、选项管理、知识点关联等功能
 */

@Service
public class QuestionService {

    private static final Logger logger = LoggerFactory.getLogger(QuestionService.class);

    @Resource
    private QuestionMapper questionMapper;
    @Resource
    private QuestionOptionMapper questionOptionMapper;
    @Resource
    private QuestionKpMapper questionKpMapper;
    @Resource
    private KnowledgePointMapper knowledgePointMapper;

    /**
     * 根据ID获取题目详情，包括选项和知识点关联
     * @param id 题目ID
     * @return 题目对象，如果不存在则返回 null
     */
    @Cacheable(value = "questions", key = "#id")
    public Question getById(Long id) {
        if (id == null) return null;
        Question q = questionMapper.selectById(id);
        if (q != null) {
            q.setOptions(questionOptionMapper.selectByQuestionId(id));
            q.setKpIds(knowledgePointMapper.selectKpIdsByQuestionId(id));
        }
        return q;
    }

    public Question getByIdForAnswer(Long id) {
        if (id == null) {
            return null;
        }
        Question q = getById(id);
        if (q != null && q.getOptions() != null) {
            q.getOptions().forEach(o -> o.setIsCorrect(null));
        }
        if (q != null) {
            q.setAnalysis(null);
        }
        return q;
    }

    @Cacheable(value = "questions", key = "#gradeId + '-' + #kpId + '-' + #difficulty + '-' + #type + '-' + #keyword + '-' + #page + '-' + #size")
    public List<Question> list(Integer gradeId, Integer kpId, Integer difficulty, String type, String keyword, int page, int size) {
        try {
            if (page < 1) page = 1;
            if (size < 1) size = 20;
            if (size > 100) size = 100;
            int offset = (page - 1) * size;
            logger.debug("查询题目列表, gradeId: {}, kpId: {}, difficulty: {}, type: {}, keyword: {}, page: {}, size: {}", 
                gradeId, kpId, difficulty, type, keyword, page, size);
            List<Question> list = questionMapper.selectByCondition(gradeId, kpId, difficulty, type, keyword, offset, size);
            logger.debug("查询到题目数量: {}", list.size());
            return list;
        } catch (Exception e) {
            logger.error("查询题目列表失败: {}", e.getMessage(), e);
            return List.of();
        }
    }

    @Cacheable(value = "questions", key = "#gradeId + '-' + #kpId + '-' + #difficulty + '-' + #type + '-' + #keyword + '-count'")
    public int count(Integer gradeId, Integer kpId, Integer difficulty, String type, String keyword) {
        try {
            logger.debug("统计题目数量, gradeId: {}, kpId: {}, difficulty: {}, type: {}, keyword: {}", 
                gradeId, kpId, difficulty, type, keyword);
            int count = questionMapper.countByCondition(gradeId, kpId, difficulty, type, keyword);
            logger.debug("题目总数: {}", count);
            return count;
        } catch (Exception e) {
            logger.error("统计题目数量失败: {}", e.getMessage(), e);
            return 0;
        }
    }

    /**
     * 保存题目信息，包括题目基本信息、选项和知识点关联
     * @param q 题目对象
     * @param teacherId 教师ID
     * @return 保存后的题目ID
     * @throws IllegalArgumentException 如果题目数据为空
     */
    @Transactional
    @CacheEvict(value = "questions", allEntries = true)
    public Long save(Question q, Long teacherId) {
        if (q == null) {
            throw new IllegalArgumentException("题目数据不能为空");
        }
        logger.debug("开始保存题目, teacherId: {}", teacherId);
        logger.debug("题目类型: {}", q.getType());
        logger.debug("题目难度: {}", q.getDifficulty());
        logger.debug("年级ID: {}", q.getGradeId());
        logger.debug("选项数量: {}", (q.getOptions() != null ? q.getOptions().size() : 0));
        logger.debug("知识点数量: {}", (q.getKpIds() != null ? q.getKpIds().size() : 0));
        
        if (q.getId() != null) {
            logger.debug("更新题目, id: {}", q.getId());
            Question existing = questionMapper.selectById(q.getId());
            if (existing == null) {
                throw new IllegalArgumentException("题目不存在");
            }
            questionMapper.update(q);
            logger.debug("删除旧选项");
            questionOptionMapper.deleteByQuestionId(q.getId());
            logger.debug("删除旧知识点关联");
            questionKpMapper.deleteByQuestionId(q.getId());
        } else {
            logger.debug("插入新题目");
            q.setTeacherId(teacherId);
            q.setStatus(1); // 设置状态为正常
            questionMapper.insert(q);
            logger.debug("新题目ID: {}", q.getId());
        }
        
        if (q.getOptions() != null && !q.getOptions().isEmpty()) {
            logger.debug("保存选项");
            q.getOptions().forEach(o -> o.setQuestionId(q.getId()));
            questionOptionMapper.insertBatch(q.getOptions());
            logger.debug("选项保存完成");
        }
        
        if (q.getKpIds() != null && !q.getKpIds().isEmpty()) {
            logger.debug("保存知识点关联");
            try {
                questionKpMapper.insertBatch(q.getId(), q.getKpIds());
                logger.debug("知识点关联保存完成");
            } catch (Exception e) {
                logger.error("知识点关联保存失败: {}", e.getMessage(), e);
                // 知识点关联失败不影响题目保存
            }
        }
        
        logger.debug("保存完成, 题目ID: {}", q.getId());
        return q.getId();
    }

    @Transactional
    @CacheEvict(value = "questions", allEntries = true)
    public int saveBatch(List<Question> questions, Long teacherId) {
        if (questions == null || questions.isEmpty()) {
            return 0;
        }
        int count = 0;
        for (Question q : questions) {
            if (q != null) {
                save(q, teacherId);
                count++;
            }
        }
        return count;
    }

    @Transactional
    @CacheEvict(value = "questions", allEntries = true)
    public void delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("题目ID不能为空");
        }
        Question existing = questionMapper.selectById(id);
        if (existing == null) {
            throw new IllegalArgumentException("题目不存在");
        }
        questionMapper.deleteById(id);
    }

    public List<Question> getByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return List.of();
        List<Question> list = questionMapper.selectByIds(ids);
        if (list.isEmpty()) return list;
        
        // 收集所有题目ID
        List<Long> questionIds = list.stream().map(Question::getId).collect(java.util.stream.Collectors.toList());
        
        // 批量查询选项
        List<QuestionOption> allOptions = questionOptionMapper.selectByQuestionIds(questionIds);
        java.util.Map<Long, java.util.List<QuestionOption>> optionsMap = allOptions.stream()
            .collect(java.util.stream.Collectors.groupingBy(QuestionOption::getQuestionId));
        
        // 批量查询知识点
        List<java.util.Map<String, Object>> kpMaps = knowledgePointMapper.selectKpIdsByQuestionIds(questionIds);
        java.util.Map<Long, java.util.List<Integer>> kpIdsMap = new java.util.HashMap<>();
        for (java.util.Map<String, Object> map : kpMaps) {
            Long qId = ((Number) map.get("question_id")).longValue();
            Integer kpId = ((Number) map.get("kp_id")).intValue();
            kpIdsMap.computeIfAbsent(qId, k -> new java.util.ArrayList<>()).add(kpId);
        }
        
        // 组装数据
        for (Question q : list) {
            q.setOptions(optionsMap.getOrDefault(q.getId(), java.util.List.of()));
            q.setKpIds(kpIdsMap.getOrDefault(q.getId(), java.util.List.of()));
        }
        return list;
    }

    /** 根据题目与用户答案判断是否正确（服务端校验） */
    public boolean checkAnswer(Long questionId, String userAnswer) {
        if (userAnswer == null) userAnswer = "";
        Question q = questionMapper.selectById(questionId);
        if (q == null) return false;
        List<QuestionOption> options = questionOptionMapper.selectByQuestionId(questionId);
        if (options == null) return false;
        java.util.Set<String> correctSet = options.stream().filter(o -> o.getIsCorrect() != null && o.getIsCorrect() == 1).map(o -> o.getOptionLabel().trim().toUpperCase()).collect(java.util.stream.Collectors.toSet());
        java.util.Set<String> userSet = java.util.Arrays.stream(userAnswer.split("[,，]")).map(String::trim).filter(s -> !s.isEmpty()).map(String::toUpperCase).collect(java.util.stream.Collectors.toSet());
        return correctSet.equals(userSet);
    }
}
