package com.space.knowledge.service;

import com.space.knowledge.entity.Level;
import com.space.knowledge.entity.UserLevelProgress;
import com.space.knowledge.mapper.LevelMapper;
import com.space.knowledge.mapper.LevelQuestionMapper;
import com.space.knowledge.mapper.UserLevelProgressMapper;
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

@Service
public class LevelService {

    private static final Logger logger = LoggerFactory.getLogger(LevelService.class);

    @Resource
    private LevelMapper levelMapper;
    @Resource
    private LevelQuestionMapper levelQuestionMapper;
    @Resource
    private UserLevelProgressMapper userLevelProgressMapper;

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
