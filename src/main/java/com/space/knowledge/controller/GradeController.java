package com.space.knowledge.controller;

import com.space.knowledge.common.Result;
import com.space.knowledge.entity.Grade;
import com.space.knowledge.mapper.GradeMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/grades")
public class GradeController {

    @Resource
    private GradeMapper gradeMapper;

    @GetMapping
    public Result<List<Grade>> list() {
        return Result.ok(gradeMapper.selectAll());
    }

    @GetMapping("/{id}")
    public Result<Grade> get(@PathVariable Integer id) {
        return Result.ok(gradeMapper.selectById(id));
    }
}
