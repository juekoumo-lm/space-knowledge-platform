package com.space.knowledge.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.space.knowledge.common.Result;
import com.space.knowledge.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(AuthInterceptor.class);

    @Resource
    private JwtUtil jwtUtil;

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        logger.debug("拦截器执行, URI: {}", request.getRequestURI());
        logger.debug("请求方法: {}", request.getMethod());
        
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            logger.debug("OPTIONS请求, 放行");
            return true;
        }
        
        String auth = request.getHeader("Authorization");
        logger.debug("Authorization头: {}", (auth != null ? auth.substring(0, Math.min(50, auth.length())) + "..." : "null"));
        
        if (auth == null || !auth.startsWith("Bearer ")) {
            logger.debug("Token无效或不存在");
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(mapper.writeValueAsString(Result.fail(401, "未登录或token无效")));
            return false;
        }
        
        String token = auth.substring(7);
        logger.debug("解析token...");
        
        if (!jwtUtil.validateToken(token)) {
            logger.debug("Token验证失败");
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(mapper.writeValueAsString(Result.fail(401, "token已过期")));
            return false;
        }
        
        Long userId = jwtUtil.getUserIdFromToken(token);
        logger.debug("从token解析userId: {}", userId);
        
        request.setAttribute("userId", userId);
        logger.debug("userId已设置到request属性中");
        
        return true;
    }
}