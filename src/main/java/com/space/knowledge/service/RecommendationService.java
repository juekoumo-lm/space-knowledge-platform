package com.space.knowledge.service;

import com.space.knowledge.entity.Question;
import com.space.knowledge.entity.UserKpMastery;
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
        if (limit <= 0) {
            return Collections.emptyList();
        }
        List<Question> cfRecommend = recommendQuestionsByCF(userId, limit);
        if (!cfRecommend.isEmpty()) {
            return cfRecommend;
        }

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
     * 协同过滤推荐（User-Based CF + 杰卡德相似系数）
     * 1) 取目标学生已作答题集A
     * 2) 找到与目标学生有共同作答的邻居学生B
     * 3) 用 J(A,B)=|A∩B|/|A∪B| 计算相似度，选TopN邻居
     * 4) 汇总邻居答对且目标学生未做过的题目，按频次与相似度加权排序
     */
    public List<Question> recommendQuestionsByCF(Long userId, int limit) {
        if (limit <= 0) {
            return Collections.emptyList();
        }
        Set<Long> doneIds = getDoneQuestionIds(userId);
        if (doneIds.isEmpty()) {
            return Collections.emptyList();
        }

        Integer userQuestionCount = attemptMapper.countDistinctQuestionsByUser(userId);
        if (userQuestionCount == null || userQuestionCount == 0) {
            return Collections.emptyList();
        }

        List<Map<String, Object>> overlaps = attemptMapper.selectNeighborOverlap(userId, 30);
        if (overlaps == null || overlaps.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, Double> neighborSimilarity = new HashMap<>();
        for (Map<String, Object> row : overlaps) {
            Long neighborId = ((Number) row.get("userId")).longValue();
            int overlap = ((Number) row.get("overlap")).intValue();
            Integer neighborQuestionCount = attemptMapper.countDistinctQuestionsByUser(neighborId);
            if (neighborQuestionCount == null || neighborQuestionCount == 0) {
                continue;
            }
            int union = userQuestionCount + neighborQuestionCount - overlap;
            if (union <= 0) {
                continue;
            }
            double jaccard = (double) overlap / union;
            if (jaccard > 0) {
                neighborSimilarity.put(neighborId, jaccard);
            }
        }

        List<Long> topNeighbors = neighborSimilarity.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(8)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        if (topNeighbors.isEmpty()) {
            return Collections.emptyList();
        }

        int candidateFetchLimit = Math.max(limit * 50, 500);
        List<Map<String, Object>> candidateRows = attemptMapper.selectNeighborCorrectQuestionPairs(topNeighbors, new ArrayList<>(doneIds), candidateFetchLimit);
        if (candidateRows == null || candidateRows.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, Double> questionScore = new HashMap<>();
        for (Map<String, Object> row : candidateRows) {
            Long neighborId = ((Number) row.get("userId")).longValue();
            Long questionId = ((Number) row.get("questionId")).longValue();
            Double similarity = neighborSimilarity.get(neighborId);
            if (similarity == null || similarity <= 0) {
                continue;
            }
            questionScore.merge(questionId, similarity, Double::sum);
        }
        if (questionScore.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> candidateIds = questionScore.entrySet().stream()
                .sorted((a, b) -> {
                    int cmp = Double.compare(b.getValue(), a.getValue());
                    if (cmp != 0) {
                        return cmp;
                    }
                    return Long.compare(a.getKey(), b.getKey());
                })
                .map(Map.Entry::getKey)
                .limit(limit)
                .collect(Collectors.toList());

        return candidateIds.stream()
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
        List<Long> doneList = attemptMapper.selectDistinctQuestionIdsByUser(userId);
        if (doneList == null || doneList.isEmpty()) {
            return Collections.emptySet();
        }
        return new HashSet<>(doneList);
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
