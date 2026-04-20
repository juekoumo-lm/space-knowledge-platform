package com.space.knowledge.controller;

import com.space.knowledge.common.Result;
import com.space.knowledge.entity.User;
import com.space.knowledge.service.AuthService;
import com.space.knowledge.mapper.UserMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/users")
public class UserController extends BaseController {

    @Resource
    private UserMapper userMapper;
    @Resource
    private AuthService authService;

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{4,32}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^.{6,64}$");
    private static final Pattern REALNAME_PATTERN = Pattern.compile("^[\u4e00-\u9fa5a-zA-Z0-9_]{0,32}$");

    @GetMapping
    public Result<List<User>> list(HttpServletRequest req,
                                  @RequestParam(required = false) Integer roleId,
                                  @RequestParam(required = false) Integer gradeId,
                                  @RequestParam(required = false) Integer classId) {
        if (!checkAdminPermission(req)) {
            return Result.fail("无权限操作");
        }

        List<User> users;
        if (roleId != null) {
            users = userMapper.selectByRole(roleId);
        } else if (gradeId != null) {
            users = userMapper.selectByGrade(gradeId);
        } else if (classId != null) {
            users = userMapper.selectByClass(classId);
        } else {
            users = userMapper.selectAll();
        }
        return Result.ok(users);
    }

    @GetMapping("/{id}")
    public Result<User> get(HttpServletRequest req, @PathVariable Long id) {
        if (!checkAdminPermission(req)) {
            return Result.fail("无权限操作");
        }
        return Result.ok(userMapper.selectById(id));
    }

    @PostMapping
    public Result<Long> create(HttpServletRequest req, @RequestBody User user) {
        if (!checkAdminPermission(req)) {
            return Result.fail("无权限操作");
        }

        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            return Result.fail("用户名不能为空");
        }
        if (!USERNAME_PATTERN.matcher(user.getUsername()).matches()) {
            return Result.fail("用户名必须为4-32位字母、数字或下划线");
        }
        if (user.getPasswordHash() == null || user.getPasswordHash().isEmpty()) {
            return Result.fail("密码不能为空");
        }
        if (!PASSWORD_PATTERN.matcher(user.getPasswordHash()).matches()) {
            return Result.fail("密码必须为6-64位字符");
        }
        if (user.getRoleId() == null || user.getRoleId() < 1 || user.getRoleId() > 3) {
            return Result.fail("角色ID无效");
        }
        if (user.getRealName() != null && !REALNAME_PATTERN.matcher(user.getRealName()).matches()) {
            return Result.fail("真实姓名格式错误");
        }

        // 加密密码
        user.setPasswordHash(AuthService.encodePassword(user.getPasswordHash()));
        userMapper.insert(user);
        return Result.ok(user.getId());
    }

    @PutMapping("/{id}")
    public Result<Void> update(HttpServletRequest req, @PathVariable Long id, @RequestBody User user) {
        if (!checkAdminPermission(req)) {
            return Result.fail("无权限操作");
        }

        User existing = userMapper.selectById(id);
        if (existing == null) {
            return Result.fail("用户不存在");
        }

        if (user.getRealName() != null && !REALNAME_PATTERN.matcher(user.getRealName()).matches()) {
            return Result.fail("真实姓名格式错误");
        }

        user.setId(id);
        user.setPasswordHash(existing.getPasswordHash()); // 保持密码不变
        userMapper.update(user);
        return Result.ok(null);
    }

    @PutMapping("/{id}/password")
    public Result<Void> updatePassword(HttpServletRequest req, @PathVariable Long id, @RequestBody String password) {
        if (!checkAdminPermission(req)) {
            return Result.fail("无权限操作");
        }

        if (password == null || password.isEmpty()) {
            return Result.fail("密码不能为空");
        }
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            return Result.fail("密码必须为6-64位字符");
        }

        String hashedPassword = AuthService.encodePassword(password);
        userMapper.updatePassword(id, hashedPassword);
        return Result.ok(null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(HttpServletRequest req, @PathVariable Long id) {
        if (!checkAdminPermission(req)) {
            return Result.fail("无权限操作");
        }

        User existing = userMapper.selectById(id);
        if (existing == null) {
            return Result.fail("用户不存在");
        }

        userMapper.delete(id);
        return Result.ok(null);
    }

    // 检查是否为管理员权限
    private boolean checkAdminPermission(HttpServletRequest req) {
        Long userId = getUserId(req);
        if (userId == null) {
            return false;
        }

        User user = userMapper.selectById(userId);
        return user != null && user.getRoleId() == 3; // 3 是管理员角色ID
    }
}