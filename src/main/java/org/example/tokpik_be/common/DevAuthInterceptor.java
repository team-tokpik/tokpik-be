package org.example.tokpik_be.common;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 개발상 편의를 위한 임시 인가 interceptor
 */
@Component
@RequiredArgsConstructor
public class DevAuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
        HttpServletResponse response,
        Object handler) throws Exception {

        request.setAttribute("userId", 1L);

        return true;
    }
}
