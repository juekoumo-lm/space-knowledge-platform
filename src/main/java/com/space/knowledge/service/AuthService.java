package com.space.knowledge.service;

import com.space.knowledge.entity.User;
import com.space.knowledge.mapper.UserMapper;
import com.space.knowledge.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Resource
    private UserMapper userMapper;
    @Resource
    private JwtUtil jwtUtil;

    @Transactional
    public Map<String, Object> login(String username, String password) {
        logger.debug("用户登录, username: {}", username);
        User user = userMapper.selectByUsername(username);
        if (user == null || !encoder.matches(password, user.getPasswordHash())) {
            logger.debug("登录失败, 用户不存在或密码错误");
            return null;
        }
        userMapper.updateLastLogin(user.getId(), LocalDateTime.now());
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRoleId());
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", toSafeUser(user));
        logger.debug("登录成功, userId: {}", user.getId());
        return data;
    }

    @Transactional
    public User register(String username, String password, Integer roleId, String realName, Integer gradeId, Integer classId) {
        logger.debug("用户注册, username: {}, roleId: {}, gradeId: {}, classId: {}", username, roleId, gradeId, classId);
        if (userMapper.selectByUsername(username) != null) {
            logger.debug("注册失败, 用户名已存在");
            return null;
        }
        
        if (roleId == null || roleId < 1 || roleId > 3) {
            roleId = 1;
        }
        
        if (realName != null && realName.length() > 32) {
            realName = realName.substring(0, 32);
        }
        
        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(encoder.encode(password));
        user.setRoleId(roleId);
        user.setRealName(realName);
        user.setGradeId(gradeId);
        user.setClassId(classId);
        user.setPoints(0);
        userMapper.insert(user);
        logger.debug("注册成功, userId: {}", user.getId());
        return user;
    }

    @Cacheable(value = "users", key = "#id")
    public User getById(Long id) {
        logger.debug("获取用户信息, id: {}", id);
        if (id == null) {
            return null;
        }
        return userMapper.selectById(id);
    }

    private Map<String, Object> toSafeUser(User u) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", u.getId());
        m.put("username", u.getUsername());
        m.put("roleId", u.getRoleId());
        m.put("realName", u.getRealName());
        m.put("gradeId", u.getGradeId());
        m.put("classId", u.getClassId());
        m.put("points", u.getPoints());
        return m;
    }

    public static String encodePassword(String raw) {
        return encoder.encode(raw);
    }
}
