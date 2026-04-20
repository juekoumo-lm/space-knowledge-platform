package com.space.knowledge.controller;

import com.space.knowledge.common.Result;
import com.space.knowledge.entity.Clazz;
import com.space.knowledge.service.ClazzService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/clazzes")
public class ClazzController extends BaseController {

    @Resource
    private ClazzService clazzService;

    @GetMapping
    public Result<List<Clazz>> list(@RequestParam(required = false) Integer gradeId) {
        return Result.ok(clazzService.listByGrade(gradeId));
    }

    @GetMapping("/{id}")
    public Result<Clazz> get(@PathVariable Integer id) {
        return Result.ok(clazzService.getById(id));
    }

    @PostMapping
    public Result<Void> create(HttpServletRequest req, @RequestBody Clazz clazz) {
        if (!checkTeacherPermission(req)) {
            return Result.fail("无权限操作");
        }
        if (clazz.getName() == null || clazz.getName().isEmpty()) {
            return Result.fail("班级名称不能为空");
        }
        if (clazz.getGradeId() == null) {
            return Result.fail("年级ID不能为空");
        }
        clazzService.save(clazz);
        return Result.ok(null);
    }

    @PutMapping("/{id}")
    public Result<Void> update(HttpServletRequest req, @PathVariable Integer id, @RequestBody Clazz clazz) {
        if (!checkTeacherPermission(req)) {
            return Result.fail("无权限操作");
        }
        if (clazz.getName() == null || clazz.getName().isEmpty()) {
            return Result.fail("班级名称不能为空");
        }
        if (clazz.getGradeId() == null) {
            return Result.fail("年级ID不能为空");
        }
        clazz.setId(id);
        clazzService.save(clazz);
        return Result.ok(null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(HttpServletRequest req, @PathVariable Integer id) {
        if (!checkTeacherPermission(req)) {
            return Result.fail("无权限操作");
        }
        clazzService.delete(id);
        return Result.ok(null);
    }
}
