package com.space.knowledge.filter;

import javax.servlet.*;
import java.io.IOException;

/**
 * 为所有响应强制设置 UTF-8，避免中文乱码。
 */
public class HtmlEncodingFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        String path = ((javax.servlet.http.HttpServletRequest) request).getRequestURI();
        if (!path.contains("/api/")) {
            response.setContentType("text/html; charset=UTF-8");
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
