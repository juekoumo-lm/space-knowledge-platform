package com.space.knowledge.controller;

import com.space.knowledge.common.Result;
import com.space.knowledge.entity.KnowledgePoint;
import com.space.knowledge.mapper.KnowledgePointMapper;
import com.space.knowledge.controller.BaseController;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/knowledge-points")
public class KnowledgePointController extends BaseController {

    @Resource
    private KnowledgePointMapper knowledgePointMapper;

    @GetMapping
    public Result<List<KnowledgePoint>> list() {
        List<KnowledgePoint> all = knowledgePointMapper.selectAll();
        if (all == null) return Result.ok(List.of());
        List<KnowledgePoint> roots = all.stream().filter(kp -> kp.getParentId() == null || kp.getParentId() == 0).collect(Collectors.toList());
        for (KnowledgePoint root : roots) {
            root.setChildren(all.stream().filter(kp -> root.getId().equals(kp.getParentId())).collect(Collectors.toList()));
        }
        return Result.ok(roots);
    }

    @GetMapping("/{id}")
    public Result<KnowledgePoint> get(@PathVariable Integer id) {
        return Result.ok(knowledgePointMapper.selectById(id));
    }

    @PostMapping
    public Result<Integer> create(HttpServletRequest req, @RequestBody KnowledgePoint kp) {
        if (!checkTeacherPermission(req)) {
            return Result.fail("无权限操作");
        }
        if (kp.getName() == null || kp.getName().isEmpty()) {
            return Result.fail("知识点名称不能为空");
        }
        knowledgePointMapper.insert(kp);
        return Result.ok(kp.getId());
    }

    @PutMapping("/{id}")
    public Result<Void> update(HttpServletRequest req, @PathVariable Integer id, @RequestBody KnowledgePoint kp) {
        if (!checkTeacherPermission(req)) {
            return Result.fail("无权限操作");
        }
        if (kp.getName() == null || kp.getName().isEmpty()) {
            return Result.fail("知识点名称不能为空");
        }
        kp.setId(id);
        knowledgePointMapper.update(kp);
        return Result.ok(null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(HttpServletRequest req, @PathVariable Integer id) {
        if (!checkTeacherPermission(req)) {
            return Result.fail("无权限操作");
        }
        // 这里可以添加删除前的检查，比如是否有题目关联
        // 暂时直接删除
        knowledgePointMapper.delete(id);
        return Result.ok(null);
    }
}
