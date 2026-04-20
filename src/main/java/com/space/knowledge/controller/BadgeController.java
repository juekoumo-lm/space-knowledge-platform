package com.space.knowledge.controller;

import com.space.knowledge.common.Result;
import com.space.knowledge.entity.Badge;
import com.space.knowledge.service.BadgeService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/badges")
public class BadgeController extends BaseController {

    @Resource
    private BadgeService badgeService;

    @GetMapping
    public Result<List<Badge>> list() {
        return Result.ok(badgeService.listAll());
    }

    @GetMapping("/{id}")
    public Result<Badge> get(@PathVariable Integer id) {
        return Result.ok(badgeService.getById(id));
    }

    @PostMapping
    public Result<Void> create(HttpServletRequest req, @RequestBody Badge badge) {
        if (!checkTeacherPermission(req)) {
            return Result.fail("无权限操作");
        }
        if (badge.getName() == null || badge.getName().isEmpty()) {
            return Result.fail("徽章名称不能为空");
        }
        if (badge.getDescription() == null || badge.getDescription().isEmpty()) {
            return Result.fail("徽章描述不能为空");
        }
        if (badge.getConditionType() == null || badge.getConditionType().isEmpty()) {
            return Result.fail("条件类型不能为空");
        }
        if (badge.getConditionValue() == null || badge.getConditionValue().isEmpty()) {
            return Result.fail("条件值不能为空");
        }
        badgeService.save(badge);
        return Result.ok(null);
    }

    @PutMapping("/{id}")
    public Result<Void> update(HttpServletRequest req, @PathVariable Integer id, @RequestBody Badge badge) {
        if (!checkTeacherPermission(req)) {
            return Result.fail("无权限操作");
        }
        if (badge.getName() == null || badge.getName().isEmpty()) {
            return Result.fail("徽章名称不能为空");
        }
        if (badge.getDescription() == null || badge.getDescription().isEmpty()) {
            return Result.fail("徽章描述不能为空");
        }
        if (badge.getConditionType() == null || badge.getConditionType().isEmpty()) {
            return Result.fail("条件类型不能为空");
        }
        if (badge.getConditionValue() == null || badge.getConditionValue().isEmpty()) {
            return Result.fail("条件值不能为空");
        }
        badge.setId(id);
        badgeService.save(badge);
        return Result.ok(null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(HttpServletRequest req, @PathVariable Integer id) {
        if (!checkTeacherPermission(req)) {
            return Result.fail("无权限操作");
        }
        badgeService.delete(id);
        return Result.ok(null);
    }

    @PostMapping("/grant")
    public Result<Void> grantBadge(HttpServletRequest req, @RequestParam Long userId, @RequestParam Integer badgeId) {
        if (!checkTeacherPermission(req)) {
            return Result.fail("无权限操作");
        }
        badgeService.grantBadgeToUser(userId, badgeId);
        return Result.ok(null);
    }

    @PostMapping("/revoke")
    public Result<Void> revokeBadge(HttpServletRequest req, @RequestParam Long userId, @RequestParam Integer badgeId) {
        if (!checkTeacherPermission(req)) {
            return Result.fail("无权限操作");
        }
        badgeService.revokeBadgeFromUser(userId, badgeId);
        return Result.ok(null);
    }
}
