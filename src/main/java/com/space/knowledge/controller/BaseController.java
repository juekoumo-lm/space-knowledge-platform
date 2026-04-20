package com.space.knowledge.controller;

import com.space.knowledge.entity.User;
import com.space.knowledge.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * 控制器基类，提供通用的权限检查和用户信息获取方法
 */

@Component
public class BaseController {

    private static final Logger logger = LoggerFactory.getLogger(BaseController.class);

    @Autowired
    private AuthService authService;

    /**
     * 获取当前用户的ID
     * @param req HttpServletRequest 对象
     * @return 当前用户的ID，如果不存在则返回 null
     */
    protected Long getUserId(HttpServletRequest req) {
        return (Long) req.getAttribute("userId");
    }

    /**
     * 检查用户是否具有教师权限
     * @param req HttpServletRequest 对象
     * @return 如果用户是教师或管理员则返回 true，否则返回 false
     */
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

    /**
     * 检查用户是否具有学生权限
     * @param req HttpServletRequest 对象
     * @return 如果用户存在则返回 true，否则返回 false
     */
    protected boolean checkStudentPermission(HttpServletRequest req) {
        Long userId = getUserId(req);
        if (userId == null) {
            return false;
        }

        User user = authService.getById(userId);
        return user != null;
    }

    /**
     * 获取当前用户信息
     * @param req HttpServletRequest 对象
     * @return 当前用户对象，如果不存在则返回 null
     */
    protected User getCurrentUser(HttpServletRequest req) {
        Long userId = getUserId(req);
        if (userId == null) {
            return null;
        }
        return authService.getById(userId);
    }
}