package com.space.knowledge.service;

import com.space.knowledge.entity.Attempt;
import com.space.knowledge.entity.UserKpMastery;
import com.space.knowledge.entity.WrongQuestion;
import com.space.knowledge.mapper.AttemptMapper;
import com.space.knowledge.mapper.KnowledgePointMapper;
import com.space.knowledge.mapper.UserKpMasteryMapper;
import com.space.knowledge.mapper.WrongQuestionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class AttemptService {

    private static final BigDecimal LR = new BigDecimal("0.2");

    @Resource
    private AttemptMapper attemptMapper;
    @Resource
    private UserKpMasteryMapper userKpMasteryMapper;
    @Resource
    private WrongQuestionMapper wrongQuestionMapper;
    @Resource
    private KnowledgePointMapper knowledgePointMapper;

    @Transactional
    public void record(Long userId, Long questionId, Integer levelId, String answer, boolean correct, int timeSpent, String source) {
        if (userId == null || questionId == null) {
            throw new IllegalArgumentException("用户ID和题目ID不能为空");
        }
        
        Attempt a = new Attempt();
        a.setUserId(userId);
        a.setQuestionId(questionId);
        a.setLevelId(levelId);
        a.setAnswer(answer);
        a.setIsCorrect(correct ? 1 : 0);
        a.setTimeSpent(timeSpent);
        a.setAttemptNo(1);
        a.setSource(source != null ? source : "practice");
        attemptMapper.insert(a);

        List<Integer> kpIds = knowledgePointMapper.selectKpIdsByQuestionId(questionId);
        if (kpIds != null) {
            for (Integer kpId : kpIds) {
                UserKpMastery m = userKpMasteryMapper.select(userId, kpId);
                BigDecimal oldScore = m != null && m.getMasteryScore() != null ? m.getMasteryScore() : BigDecimal.ZERO;
                BigDecimal reward = correct ? BigDecimal.ONE : BigDecimal.ZERO;
                BigDecimal newScore = oldScore.add(LR.multiply(reward.subtract(oldScore))).setScale(4, RoundingMode.HALF_UP);
                if (newScore.compareTo(BigDecimal.ONE) > 0) newScore = BigDecimal.ONE;
                if (newScore.compareTo(BigDecimal.ZERO) < 0) newScore = BigDecimal.ZERO;
                UserKpMastery upd = new UserKpMastery();
                upd.setUserId(userId);
                upd.setKpId(kpId);
                upd.setMasteryScore(newScore);
                userKpMasteryMapper.insertOrUpdate(upd);
            }
        }

        if (!correct) {
            WrongQuestion wq = new WrongQuestion();
            wq.setUserId(userId);
            wq.setQuestionId(questionId);
            wrongQuestionMapper.insert(wq);
        }
    }

    public List<Attempt> recentByUser(Long userId, int n) {
        return attemptMapper.selectRecentByUserId(userId, n);
    }

    public int countCorrectSince(Long userId, java.time.LocalDateTime since) {
        return attemptMapper.countCorrectByUser(userId, since);
    }

    public int countSince(Long userId, java.time.LocalDateTime since) {
        return attemptMapper.countByUserSince(userId, since);
    }
}
