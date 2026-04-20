package com.space.knowledge.controller;

import com.space.knowledge.common.Result;
import com.space.knowledge.entity.Question;
import com.space.knowledge.entity.Level;
import com.space.knowledge.entity.User;
import com.space.knowledge.service.AuthService;
import com.space.knowledge.service.QuestionService;
import com.space.knowledge.service.LevelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/teacher")
public class TeacherController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(TeacherController.class);

    @Resource
    private QuestionService questionService;
    @Resource
    private LevelService levelService;
    @Resource
    private AuthService authService;

    @Override
    protected boolean checkTeacherPermission(HttpServletRequest req) {
        Long userId = getUserId(req);
        if (userId == null) {
            return false;
        }

        if (authService == null) {
            logger.error("authService is null, cannot check permission");
            return false;
        }

        User user = authService.getById(userId);
        if (user == null) {
            return false;
        }

        return user.isTeacher() || user.isAdmin();
    }

    @Override
    protected Long getUserId(HttpServletRequest req) {
        return (Long) req.getAttribute("userId");
    }

    @GetMapping("/questions")
    public Result<Map<String, Object>> listQuestions(HttpServletRequest req,
            @RequestParam(required = false) Integer gradeId,
            @RequestParam(required = false) Integer kpId,
            @RequestParam(required = false) Integer difficulty,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        if (!checkTeacherPermission(req)) {
            return Result.fail("无权限操作");
        }
        if (page < 1) page = 1;
        if (size < 1 || size > 100) size = 20;
        List<Question> list = questionService.list(gradeId, kpId, difficulty, type, keyword, page, size);
        int total = questionService.count(gradeId, kpId, difficulty, type, keyword);
        return Result.ok(Map.of("list", list, "total", total));
    }

    @GetMapping("/questions/{id}")
    public Result<Question> getQuestion(HttpServletRequest req, @PathVariable Long id) {
        if (!checkTeacherPermission(req)) {
            return Result.fail("无权限操作");
        }
        if (id == null) {
            return Result.fail("题目ID不能为空");
        }
        return Result.ok(questionService.getById(id));
    }

    @PostMapping("/questions")
    public Result<Long> saveQuestion(HttpServletRequest req, @RequestBody @Valid Question q) {
        if (!checkTeacherPermission(req)) {
            return Result.fail("无权限操作");
        }
        Long userId = getUserId(req);
        Long id = questionService.save(q, userId);
        return Result.ok(id);
    }

    @DeleteMapping("/questions/{id}")
    public Result<Void> deleteQuestion(HttpServletRequest req, @PathVariable Long id) {
        if (!checkTeacherPermission(req)) {
            return Result.fail("无权限操作");
        }
        if (id == null) {
            return Result.fail("题目ID不能为空");
        }
        questionService.delete(id);
        return Result.ok(null);
    }

    // 关卡管理相关API
    @GetMapping("/levels")
    public Result<List<Level>> listLevels(HttpServletRequest req, @RequestParam(required = false) Integer gradeId) {
        if (!checkTeacherPermission(req)) {
            return Result.fail("无权限操作");
        }
        return Result.ok(levelService.listByGrade(gradeId));
    }

    @GetMapping("/levels/{id}")
    public Result<Level> getLevel(HttpServletRequest req, @PathVariable Integer id) {
        if (!checkTeacherPermission(req)) {
            return Result.fail("无权限操作");
        }
        return Result.ok(levelService.getById(id));
    }

    @PostMapping("/levels")
    public Result<Void> saveLevel(HttpServletRequest req, @RequestBody Level level) {
        if (!checkTeacherPermission(req)) {
            return Result.fail("无权限操作");
        }
        levelService.save(level);
        return Result.ok(null);
    }

    @DeleteMapping("/levels/{id}")
    public Result<Void> deleteLevel(HttpServletRequest req, @PathVariable Integer id) {
        if (!checkTeacherPermission(req)) {
            return Result.fail("无权限操作");
        }
        if (id == null) {
            return Result.fail("关卡ID不能为空");
        }
        levelService.delete(id);
        return Result.ok(null);
    }

    @GetMapping("/levels/{id}/questions")
    public Result<List<Question>> getLevelQuestions(HttpServletRequest req, @PathVariable Integer id) {
        if (!checkTeacherPermission(req)) {
            return Result.fail("无权限操作");
        }
        List<Long> questionIds = levelService.getLevelQuestionIds(id);
        List<Question> questions = questionService.getByIds(questionIds);
        return Result.ok(questions);
    }

    @PostMapping("/levels/{id}/questions")
    public Result<Void> setLevelQuestions(HttpServletRequest req, @PathVariable Integer id, @RequestBody List<Long> questionIds) {
        if (!checkTeacherPermission(req)) {
            return Result.fail("无权限操作");
        }
        levelService.setLevelQuestions(id, questionIds);
        return Result.ok(null);
    }

}