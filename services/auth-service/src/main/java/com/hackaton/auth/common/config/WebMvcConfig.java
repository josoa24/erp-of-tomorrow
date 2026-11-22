package com.hackaton.auth.common.config;

import com.hackaton.auth.common.security.AdminOnlyInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final AdminOnlyInterceptor adminOnlyInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminOnlyInterceptor)
                .addPathPatterns("/users", "/users/**")
                .excludePathPatterns("/users/me", "/users/me/**");
    }
}
