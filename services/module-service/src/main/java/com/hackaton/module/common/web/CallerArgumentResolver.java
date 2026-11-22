package com.hackaton.module.common.web;

import com.hackaton.module.common.security.Caller;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class CallerArgumentResolver implements HandlerMethodArgumentResolver {

    static final String USER_EMAIL = "X-User-Email";
    static final String USER_ROLE = "X-User-Role";

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return Caller.class.equals(parameter.getParameterType());
    }

    @Override
    public Caller resolveArgument(MethodParameter parameter, ModelAndViewContainer container,
            NativeWebRequest request, WebDataBinderFactory binderFactory) {
        return new Caller(request.getHeader(USER_EMAIL), request.getHeader(USER_ROLE));
    }
}
