package org.example.tokpik_be.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class TestAuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
        HttpServletResponse response,
        Object handler) throws Exception {

        request.setAttribute("userId", 1L);

        return true;
    }
}
