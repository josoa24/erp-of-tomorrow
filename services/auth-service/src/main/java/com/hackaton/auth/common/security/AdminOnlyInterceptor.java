package com.hackaton.auth.common.security;

import com.hackaton.auth.common.exception.ForbiddenException;
import com.hackaton.auth.common.identity.IdentityHeaders;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminOnlyInterceptor implements HandlerInterceptor {

    private static final String ADMIN = "ADMIN";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!ADMIN.equals(request.getHeader(IdentityHeaders.USER_ROLE))) {
            throw new ForbiddenException("Action réservée aux administrateurs");
        }
        return true;
    }
}
