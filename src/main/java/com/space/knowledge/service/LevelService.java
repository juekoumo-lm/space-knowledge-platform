package com.space.knowledge.service;

import com.space.knowledge.entity.Level;
import com.space.knowledge.entity.Attempt;
import com.space.knowledge.entity.UserLevelProgress;
import com.space.knowledge.mapper.AttemptMapper;
import com.space.knowledge.mapper.KnowledgePointMapper;
import com.space.knowledge.mapper.LevelMapper;
import com.space.knowledge.mapper.LevelQuestionMapper;
import com.space.knowledge.mapper.UserMapper;
import com.space.knowledge.mapper.UserLevelProgressMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LevelService {

    private static final Logger logger = LoggerFactory.getLogger(LevelService.class);

    @Resource
    private LevelMapper levelMapper;
    @Resource
    private LevelQuestionMapper levelQuestionMapper;
    @Resource
    private UserLevelProgressMapper userLevelProgressMapper;
    @Resource
    private AttemptMapper attemptMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private KnowledgePointMapper knowledgePointMapper;

    @Cacheable(value = "levels", key = "#id")
    public Level getById(Integer id) {
        logger.debug("获取关卡详情, id: {}", id);
        Level l = levelMapper.selectById(id);
        if (l != null) {
            l.setQuestionIds(levelMapper.selectQuestionIdsByLevelId(id).stream().map(Long::valueOf).collect(Collectors.toList()));
        }
        return l;
    }

    @Cacheable(value = "levels", key = "#gradeId")
    public List<Level> listByGrade(Integer gradeId) {
        logger.debug("获取年级关卡列表, gradeId: {}", gradeId);
        return levelMapper.selectByGradeId(gradeId);
    }

    public List<Long> getLevelQuestionIds(Integer levelId) {
        logger.debug("获取关卡题目ID列表, levelId: {}", levelId);
        return levelMapper.selectQuestionIdsByLevelId(levelId).stream().map(Long::valueOf).collect(Collectors.toList());
    }

    public List<UserLevelProgress> getProgressByUserId(Long userId) {
        logger.debug("获取用户关卡进度, userId: {}", userId);
        return userLevelProgressMapper.selectByUserId(userId);
    }

    public UserLevelProgress getProgress(Long userId, Integer levelId) {
        logger.debug("获取用户关卡进度, userId: {}, levelId: {}", userId, levelId);
        return userLevelProgressMapper.select(userId, levelId);
    }

    @Transactional
    public void saveProgress(Long userId, Integer levelId, java.math.BigDecimal score, boolean passed, Integer timeSpent) {
        logger.debug("保存用户关卡进度, userId: {}, levelId: {}, score: {}, passed: {}, timeSpent: {}", userId, levelId, score, passed, timeSpent);
        UserLevelProgress p = new UserLevelProgress();
        p.setUserId(userId);
        p.setLevelId(levelId);
        p.setScore(score);
        p.setPassed(passed ? 1 : 0);
        p.setBestTimeSpent(timeSpent);
        userLevelProgressMapper.insertOrUpdate(p);
    }

    /**
     * 关卡结束后的自适应路径分析。
     */
    public Map<String, Object> analyzePerformance(Long userId, Integer levelId) {
        Map<String, Object> result = new HashMap<>();
        Level level = getById(levelId);
        if (level == null) {
            result.put("nextLevelSuggestion", "NORMAL_UNLOCK");
            return result;
        }

        List<Attempt> attempts = attemptMapper.selectRecentByUserAndLevel(userId, levelId, Math.max(level.getQuestionCount() != null ? level.getQuestionCount() : 10, 10));
        if (attempts == null || attempts.isEmpty()) {
            result.put("nextLevelSuggestion", "NORMAL_UNLOCK");
            return result;
        }

        int total = attempts.size();
        long correct = attempts.stream().filter(a -> a.getIsCorrect() != null && a.getIsCorrect() == 1).count();
        double accuracy = (double) correct / total;
        double avgTime = attempts.stream().filter(a -> a.getTimeSpent() != null && a.getTimeSpent() > 0).mapToInt(Attempt::getTimeSpent).average().orElse(0);

        com.space.knowledge.entity.User user = userMapper.selectById(userId);
        Integer gradeId = user != null ? user.getGradeId() : null;
        Double gradeAvgTime = gradeId != null ? attemptMapper.avgTimeSpentByGradeAndLevel(gradeId, levelId) : null;
        if (gradeAvgTime == null || gradeAvgTime <= 0) {
            gradeAvgTime = avgTime;
        }

        int masteryStreak = calculateKnowledgePointStreak(attempts);
        String suggestion;
        Integer nextLevelId = levelMapper.selectNextLevelId(level.getGradeId(), level.getSortOrder() != null ? level.getSortOrder() : 0, level.getId());

        if (accuracy >= 1.0 && avgTime > 0 && avgTime <= gradeAvgTime * 0.8 && masteryStreak >= 3) {
            suggestion = "ELITE_CHALLENGE";
        } else if (accuracy < 0.4) {
            suggestion = "RECHALLENGE";
        } else {
            suggestion = "NORMAL_UNLOCK";
        }

        result.put("accuracy", accuracy);
        result.put("avgTime", avgTime);
        result.put("gradeAvgTime", gradeAvgTime);
        result.put("masteryStreak", masteryStreak);
        result.put("nextLevelId", nextLevelId);
        result.put("nextLevelSuggestion", suggestion);
        return result;
    }

    private int calculateKnowledgePointStreak(List<Attempt> attempts) {
        Set<Long> questionIds = attempts.stream()
                .map(Attempt::getQuestionId)
                .collect(Collectors.toCollection(HashSet::new));
        Map<Long, List<Integer>> questionKpMap = new HashMap<>();
        if (!questionIds.isEmpty()) {
            List<Map<String, Object>> rows = knowledgePointMapper.selectKpIdsByQuestionIds(questionIds.stream().collect(Collectors.toList()));
            for (Map<String, Object> row : rows) {
                Long questionId = ((Number) row.get("question_id")).longValue();
                Integer kpId = ((Number) row.get("kp_id")).intValue();
                questionKpMap.computeIfAbsent(questionId, k -> new java.util.ArrayList<>()).add(kpId);
            }
        }

        Map<Integer, Integer> streakMap = new HashMap<>();
        int best = 0;
        for (int i = attempts.size() - 1; i >= 0; i--) {
            Attempt attempt = attempts.get(i);
            List<Integer> kpIds = questionKpMap.get(attempt.getQuestionId());
            if (kpIds == null || kpIds.isEmpty()) {
                continue;
            }
            for (Integer kpId : kpIds) {
                int next = (attempt.getIsCorrect() != null && attempt.getIsCorrect() == 1) ? streakMap.getOrDefault(kpId, 0) + 1 : 0;
                streakMap.put(kpId, next);
                if (next > best) {
                    best = next;
                }
            }
        }
        return best;
    }

    @Transactional
    @CacheEvict(value = "levels", key = "#levelId")
    public void setLevelQuestions(Integer levelId, List<Long> questionIds) {
        logger.debug("设置关卡题目, levelId: {}, questionIds: {}", levelId, questionIds);
        levelQuestionMapper.deleteByLevelId(levelId);
        if (questionIds != null && !questionIds.isEmpty()) {
            levelQuestionMapper.insertBatch(levelId, questionIds);
        }
    }

    @Transactional
    @CacheEvict(value = "levels", allEntries = true)
    public void save(Level level) {
        logger.debug("保存关卡, id: {}, name: {}", level.getId(), level.getName());
        if (level.getId() == null) {
            levelMapper.insert(level);
        } else {
            levelMapper.update(level);
        }
    }

    @Transactional
    @CacheEvict(value = "levels", allEntries = true)
    public void delete(Integer levelId) {
        logger.debug("删除关卡, id: {}", levelId);
        // 先删除关卡与题目的关联
        levelQuestionMapper.deleteByLevelId(levelId);
        // 再删除关卡本身
        levelMapper.delete(levelId);
    }
}
