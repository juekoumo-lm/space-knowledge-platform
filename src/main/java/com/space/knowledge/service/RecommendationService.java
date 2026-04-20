package com.space.knowledge.service;

import com.space.knowledge.entity.Question;
import com.space.knowledge.entity.User;
import com.space.knowledge.entity.UserKpMastery;
import com.space.knowledge.entity.Attempt;
import com.space.knowledge.entity.WrongQuestion;
import com.space.knowledge.mapper.AttemptMapper;
import com.space.knowledge.mapper.QuestionMapper;
import com.space.knowledge.mapper.UserKpMasteryMapper;
import com.space.knowledge.mapper.WrongQuestionMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 混合推荐：规则优先级 + 内容匹配（年级、难度、错题、知识点掌握度）
 * score = α*contentSim + β*recencyBoost + γ*(1-doneBefore) + δ*difficultyMatch
 */
@Service
public class RecommendationService {

    @Resource
    private QuestionMapper questionMapper;
    @Resource
    private AttemptMapper attemptMapper;
    @Resource
    private WrongQuestionMapper wrongQuestionMapper;
    @Resource
    private UserKpMasteryMapper userKpMasteryMapper;
    @Resource
    private QuestionService questionService;
    @Resource
    private AttemptService attemptService;

    private static final int RECENT_N = 10;
    private static final double ACCURACY_UP = 0.85;
    private static final double ACCURACY_DOWN = 0.50;

    /**
     * 今日推荐 / 练习推荐：优先错题、未做、与能力匹配难度
     */
    public List<Question> recommendForUser(Long userId, Integer gradeId, int limit) {
        User user = new User();
        user.setId(userId);
        user.setGradeId(gradeId);
        int suggestedDifficulty = suggestDifficulty(userId);
        Set<Long> doneIds = getDoneQuestionIds(userId);
        List<Long> wrongIds = getWrongQuestionIds(userId);
        List<Integer> weakKpIds = getWeakKpIds(userId);

        Set<Long> candidateSet = new LinkedHashSet<>(wrongIds);
        List<Question> byKp = gradeId != null
                ? questionMapper.selectByCondition(gradeId, weakKpIds.isEmpty() ? null : weakKpIds.get(0), suggestedDifficulty, null, null, 0, 50)
                : questionMapper.selectByCondition(null, null, suggestedDifficulty, null, null, 0, 50);
        for (Question q : byKp) {
            if (!doneIds.contains(q.getId())) candidateSet.add(q.getId());
        }
        List<Long> candidateIds = new ArrayList<>(candidateSet);
        if (candidateIds.size() > limit) {
            Collections.shuffle(candidateIds);
            candidateIds = candidateIds.subList(0, limit);
        }
        List<Long> finalIds = candidateIds;
        return finalIds.stream()
                .map(id -> questionService.getByIdForAnswer(id))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 自适应难度：最近N题正确率与用时
     */
    public int suggestDifficulty(Long userId) {
        LocalDateTime since = LocalDateTime.now().minusDays(7);
        int total = attemptService.countSince(userId, since);
        if (total < RECENT_N) return 1;
        int correct = attemptService.countCorrectSince(userId, since);
        double accuracy = (double) correct / total;
        if (accuracy >= ACCURACY_UP) return 2;
        if (accuracy <= ACCURACY_DOWN) return 0;
        return 1;
    }

    private Set<Long> getDoneQuestionIds(Long userId) {
        return attemptMapper.selectByUserId(userId, 500).stream()
                .map(Attempt::getQuestionId)
                .collect(Collectors.toSet());
    }

    private List<Long> getWrongQuestionIds(Long userId) {
        return wrongQuestionMapper.selectByUserId(userId, 0, 100).stream()
                .map(WrongQuestion::getQuestionId)
                .collect(Collectors.toList());
    }

    private List<Integer> getWeakKpIds(Long userId) {
        List<UserKpMastery> list = userKpMasteryMapper.selectByUserId(userId);
        return list.stream()
                .filter(m -> m.getMasteryScore() != null && m.getMasteryScore().doubleValue() < 0.5)
                .map(UserKpMastery::getKpId)
                .collect(Collectors.toList());
    }
}
