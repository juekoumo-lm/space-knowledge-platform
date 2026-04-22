package com.space.knowledge.controller;

import com.space.knowledge.common.Result;
import com.space.knowledge.entity.*;
import com.space.knowledge.service.*;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/student")
public class StudentController {

    @Resource
    private LevelService levelService;
    @Resource
    private QuestionService questionService;
    @Resource
    private AttemptService attemptService;
    @Resource
    private RecommendationService recommendationService;
    @Resource
    private com.space.knowledge.mapper.WrongQuestionMapper wrongQuestionMapper;
    @Resource
    private com.space.knowledge.mapper.UserMapper userMapper;
    @Resource
    private com.space.knowledge.mapper.BadgeMapper badgeMapper;
    @Resource
    private com.space.knowledge.mapper.UserKpMasteryMapper userKpMasteryMapper;

    private Long userId(HttpServletRequest req) {
        return (Long) req.getAttribute("userId");
    }

    private Integer parseInteger(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private java.math.BigDecimal parseBigDecimal(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return java.math.BigDecimal.valueOf(((Number) value).doubleValue());
        }
        try {
            return new java.math.BigDecimal(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @GetMapping("/levels")
    public Result<List<Level>> levels(HttpServletRequest req, @RequestParam Integer gradeId) {
        Long userId = userId(req);
        if (userId == null) {
            return Result.fail("用户未登录");
        }
        User u = userMapper.selectById(userId);
        if (u == null) {
            return Result.fail("用户不存在");
        }
        
        // 年级权限检查
        if (gradeId != null && !gradeId.equals(u.getGradeId())) {
            return Result.fail("无权访问其他年级的关卡");
        }
        
        return Result.ok(levelService.listByGrade(gradeId));
    }

    @GetMapping("/level/{id}")
    public Result<Level> level(HttpServletRequest req, @PathVariable Integer id) {
        Long userId = userId(req);
        if (userId == null) {
            return Result.fail("用户未登录");
        }
        User u = userMapper.selectById(userId);
        if (u == null) {
            return Result.fail("用户不存在");
        }
        
        Level level = levelService.getById(id);
        if (level == null) {
            return Result.fail("关卡不存在");
        }
        
        // 年级权限检查
        if (level.getGradeId() != null && !level.getGradeId().equals(u.getGradeId())) {
            return Result.fail("无权访问其他年级的关卡");
        }
        
        return Result.ok(level);
    }

    @GetMapping("/question/{id}")
    public Result<Question> questionForPractice(HttpServletRequest req, @PathVariable Long id) {
        Long userId = userId(req);
        if (userId == null) {
            return Result.fail("用户未登录");
        }
        User u = userMapper.selectById(userId);
        if (u == null) {
            return Result.fail("用户不存在");
        }
        
        Question q = questionService.getByIdForAnswer(id);
        if (q == null) {
            return Result.fail("题目不存在");
        }
        
        // 年级权限检查
        if (q.getGradeId() != null && !q.getGradeId().equals(u.getGradeId())) {
            return Result.fail("无权访问其他年级的题目");
        }
        
        return Result.ok(q);
    }

    @GetMapping("/question/{id}/analysis")
    public Result<String> questionAnalysis(HttpServletRequest req, @PathVariable Long id) {
        Long userId = userId(req);
        if (userId == null) {
            return Result.fail("用户未登录");
        }
        User u = userMapper.selectById(userId);
        if (u == null) {
            return Result.fail("用户不存在");
        }
        
        Question q = questionService.getById(id);
        if (q == null) {
            return Result.fail("题目不存在");
        }
        
        // 年级权限检查
        if (q.getGradeId() != null && !q.getGradeId().equals(u.getGradeId())) {
            return Result.fail("无权访问其他年级的题目");
        }
        
        return Result.ok(q != null ? q.getAnalysis() : null);
    }

    @GetMapping("/level/{id}/questions")
    public Result<List<Question>> levelQuestions(HttpServletRequest req, @PathVariable Integer id) {
        Long userId = userId(req);
        if (userId == null) {
            return Result.fail("用户未登录");
        }
        User u = userMapper.selectById(userId);
        if (u == null) {
            return Result.fail("用户不存在");
        }
        
        List<Long> ids = levelService.getLevelQuestionIds(id);
        List<Question> list = questionService.getByIds(ids).stream()
                .map(q -> questionService.getByIdForAnswer(q.getId()))
                .filter(q -> q != null && (q.getGradeId() == null || q.getGradeId().equals(u.getGradeId())))
                .collect(Collectors.toList());
        return Result.ok(list);
    }

    @PostMapping("/answer")
    public Result<Map<String, Object>> submitAnswer(HttpServletRequest req, @RequestBody Map<String, Object> body) {
        Long userId = userId(req);
        if (userId == null) {
            return Result.fail("用户未登录");
        }
        
        // 安全的参数类型转换
        Long questionId = null;
        Object questionIdObj = body.get("questionId");
        if (questionIdObj == null) {
            return Result.fail("题目ID不能为空");
        }
        try {
            if (questionIdObj instanceof Number) {
                questionId = ((Number) questionIdObj).longValue();
            } else {
                questionId = Long.parseLong(questionIdObj.toString());
            }
        } catch (NumberFormatException e) {
            return Result.fail("题目ID格式错误");
        }
        
        Integer levelId = null;
        Object levelIdObj = body.get("levelId");
        if (levelIdObj != null) {
            try {
                if (levelIdObj instanceof Number) {
                    levelId = ((Number) levelIdObj).intValue();
                } else {
                    levelId = Integer.parseInt(levelIdObj.toString());
                }
            } catch (NumberFormatException e) {
                return Result.fail("关卡ID格式错误");
            }
        }
        
        String answer = body.get("answer") != null ? body.get("answer").toString() : "";
        
        Integer timeSpent = 0;
        Object timeSpentObj = body.get("timeSpent");
        if (timeSpentObj != null) {
            try {
                if (timeSpentObj instanceof Number) {
                    timeSpent = ((Number) timeSpentObj).intValue();
                } else {
                    timeSpent = Integer.parseInt(timeSpentObj.toString());
                }
            } catch (NumberFormatException e) {
                // 忽略错误，使用默认值0
            }
        }
        
        String source = (String) body.get("source");
        if (source == null) source = levelId != null ? "level" : "practice";
        
        boolean correct = questionService.checkAnswer(questionId, answer);
        attemptService.record(userId, questionId, levelId, answer, correct, timeSpent, source);
        return Result.ok(java.util.Map.of("correct", correct));
    }

    @PostMapping("/level/{levelId}/complete")
    public Result<Map<String, Object>> completeLevel(HttpServletRequest req, @PathVariable Integer levelId, @RequestBody Map<String, Object> body) {
        Long userId = userId(req);
        if (userId == null) {
            return Result.fail("用户未登录");
        }
        if (levelId == null) {
            return Result.fail("关卡ID不能为空");
        }
        java.math.BigDecimal score = parseBigDecimal(body.get("score"));
        if (body.get("score") != null && score == null) {
            return Result.fail("分数格式错误");
        }
        Boolean passed = (Boolean) body.get("passed");
        Integer timeSpent = parseInteger(body.get("timeSpent"));
        if (body.get("timeSpent") != null && timeSpent == null) {
            return Result.fail("用时格式错误");
        }
        levelService.saveProgress(userId, levelId, score, Boolean.TRUE.equals(passed), timeSpent);
        Map<String, Object> suggestion = levelService.analyzePerformance(userId, levelId);
        return Result.ok(suggestion);
    }

    @GetMapping("/recommend")
    public Result<List<Question>> recommend(HttpServletRequest req, @RequestParam(required = false) Integer gradeId, @RequestParam(defaultValue = "10") int limit) {
        Long userId = userId(req);
        if (userId == null) {
            return Result.fail("用户未登录");
        }
        User u = userMapper.selectById(userId);
        if (gradeId == null && u != null) gradeId = u.getGradeId();
        if (limit < 1 || limit > 100) {
            limit = 10;
        }
        return Result.ok(recommendationService.recommendForUser(userId, gradeId, limit));
    }

    @GetMapping("/wrong")
    public Result<List<WrongQuestion>> wrongList(HttpServletRequest req, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int size) {
        Long userId = userId(req);
        if (userId == null) {
            return Result.fail("用户未登录");
        }
        if (page < 1) page = 1;
        if (size < 1 || size > 100) size = 20;
        int offset = (page - 1) * size;
        List<WrongQuestion> list = wrongQuestionMapper.selectByUserId(userId, offset, size);
        if (list != null) {
            list.forEach(w -> w.setQuestion(questionService.getById(w.getQuestionId())));
        }
        return Result.ok(list);
    }

    @DeleteMapping("/wrong/{questionId}")
    public Result<Void> removeWrong(HttpServletRequest req, @PathVariable Long questionId) {
        Long userId = userId(req);
        if (userId == null) {
            return Result.fail("用户未登录");
        }
        if (questionId == null) {
            return Result.fail("题目ID不能为空");
        }
        wrongQuestionMapper.delete(userId, questionId);
        return Result.ok(null);
    }

    @GetMapping("/progress")
    public Result<List<UserLevelProgress>> progress(HttpServletRequest req) {
        Long userId = userId(req);
        if (userId == null) {
            return Result.fail("用户未登录");
        }
        return Result.ok(levelService.getProgressByUserId(userId));
    }

    @GetMapping("/mastery")
    public Result<List<UserKpMastery>> mastery(HttpServletRequest req) {
        Long userId = userId(req);
        if (userId == null) {
            return Result.fail("用户未登录");
        }
        return Result.ok(userKpMasteryMapper.selectByUserId(userId));
    }

    @GetMapping("/badges")
    public Result<List<Badge>> badges(HttpServletRequest req) {
        Long userId = userId(req);
        if (userId == null) {
            return Result.fail("用户未登录");
        }
        List<Integer> obtainedIds = badgeMapper.selectBadgeIdsByUserId(userId);
        List<Badge> all = badgeMapper.selectAll();
        if (all != null) {
            all.forEach(b -> b.setObtained(obtainedIds != null && obtainedIds.contains(b.getId())));
        }
        return Result.ok(all);
    }

    @GetMapping("/dashboard")
    public Result<Map<String, Object>> dashboard(HttpServletRequest req) {
        Long userId = userId(req);
        if (userId == null) {
            return Result.fail("用户未登录");
        }
        User u = userMapper.selectById(userId);
        List<UserLevelProgress> progress = levelService.getProgressByUserId(userId);
        int passedCount = progress != null ? (int) progress.stream().filter(p -> p.getPassed() != null && p.getPassed() == 1).count() : 0;
        List<Question> recommend = recommendationService.recommendForUser(userId, u != null ? u.getGradeId() : null, 5);
        Map<String, Object> map = new HashMap<>();
        map.put("user", u);
        map.put("passedLevels", passedCount);
        map.put("recommend", recommend);
        return Result.ok(map);
    }
}
