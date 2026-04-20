package com.space.knowledge.controller;

import com.space.knowledge.common.Result;
import com.space.knowledge.entity.Assignment;
import com.space.knowledge.service.AssignmentService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/assignments")
public class AssignmentController extends BaseController {

    @Resource
    private AssignmentService assignmentService;

    @GetMapping
    public Result<List<Assignment>> list(HttpServletRequest req, @RequestParam(required = false) Integer classId) {
        Long userId = getUserId(req);
        if (userId == null) {
            return Result.fail("用户未登录");
        }
        if (classId != null) {
            return Result.ok(assignmentService.listByClass(classId));
        }
        return Result.ok(assignmentService.listByTeacher(userId));
    }

    @GetMapping("/{id}")
    public Result<Assignment> get(@PathVariable Long id) {
        return Result.ok(assignmentService.getById(id));
    }

    @PostMapping
    public Result<Void> create(HttpServletRequest req, @RequestBody Assignment assignment) {
        if (!checkTeacherPermission(req)) {
            return Result.fail("无权限操作");
        }
        if (assignment.getTitle() == null || assignment.getTitle().isEmpty()) {
            return Result.fail("作业标题不能为空");
        }
        if (assignment.getClassId() == null && assignment.getLevelId() == null) {
            return Result.fail("班级ID或关卡ID不能为空");
        }
        assignment.setTeacherId(getUserId(req));
        assignmentService.save(assignment);
        return Result.ok(null);
    }

    @PutMapping("/{id}")
    public Result<Void> update(HttpServletRequest req, @PathVariable Long id, @RequestBody Assignment assignment) {
        if (!checkTeacherPermission(req)) {
            return Result.fail("无权限操作");
        }
        if (assignment.getTitle() == null || assignment.getTitle().isEmpty()) {
            return Result.fail("作业标题不能为空");
        }
        if (assignment.getClassId() == null && assignment.getLevelId() == null) {
            return Result.fail("班级ID或关卡ID不能为空");
        }
        assignment.setId(id);
        assignment.setTeacherId(getUserId(req));
        assignmentService.save(assignment);
        return Result.ok(null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(HttpServletRequest req, @PathVariable Long id) {
        if (!checkTeacherPermission(req)) {
            return Result.fail("无权限操作");
        }
        assignmentService.delete(id);
        return Result.ok(null);
    }
}
