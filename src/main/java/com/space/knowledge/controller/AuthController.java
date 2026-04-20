package com.space.knowledge.controller;

import com.space.knowledge.common.Result;
import com.space.knowledge.entity.User;
import com.space.knowledge.service.AuthService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Resource
    private AuthService authService;

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{4,32}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^.{6,64}$");
    private static final Pattern REALNAME_PATTERN = Pattern.compile("^[\u4e00-\u9fa5a-zA-Z0-9_]{0,32}$");

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        if (username == null || password == null) {
            return Result.fail("用户名和密码不能为空");
        }
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            return Result.fail("用户名格式错误");
        }
        Map<String, Object> data = authService.login(username, password);
        if (data == null) {
            return Result.fail("用户名或密码错误");
        }
        return Result.ok(data);
    }

    @PostMapping("/register")
    public Result<Map<String, Object>> register(@RequestBody Map<String, Object> body) {
        String username = (String) body.get("username");
        String password = (String) body.get("password");
        Integer roleId = body.get("roleId") != null ? ((Number) body.get("roleId")).intValue() : 1;
        String realName = (String) body.get("realName");
        Integer gradeId = body.get("gradeId") != null ? ((Number) body.get("gradeId")).intValue() : null;
        Integer classId = body.get("classId") != null ? ((Number) body.get("classId")).intValue() : null;
        
        if (username == null || password == null) {
            return Result.fail("用户名和密码不能为空");
        }
        
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            return Result.fail("用户名必须为4-32位字母、数字或下划线");
        }
        
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            return Result.fail("密码必须为6-64位字符");
        }
        
        if (realName != null && !realName.isEmpty() && !REALNAME_PATTERN.matcher(realName).matches()) {
            return Result.fail("真实姓名格式错误");
        }
        
        if (roleId == null || roleId < 1 || roleId > 3) {
            roleId = 1;
        }
        
        if (roleId == 1 && gradeId == null) {
            return Result.fail("学生角色必须选择年级");
        }
        
        User user = authService.register(username, password, roleId, realName, gradeId, classId);
        if (user == null) {
            return Result.fail("用户名已存在");
        }
        Map<String, Object> data = authService.login(username, password);
        return Result.ok(data);
    }

    @GetMapping("/forgot-password")
    public Result<String> forgotPassword(@RequestParam String username) {
        if (username == null || !USERNAME_PATTERN.matcher(username).matches()) {
            return Result.fail("用户名格式错误");
        }
        return Result.ok("请联系管理员重置密码");
    }
}
