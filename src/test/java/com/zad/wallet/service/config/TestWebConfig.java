package com.zad.wallet.service.config;

import com.zad.wallet.interceptor.JwtInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class TestWebConfig implements WebMvcConfigurer {
    private final JwtInterceptor jwtInterceptor;

    public TestWebConfig(JwtInterceptor jwtInterceptor) {
        this.jwtInterceptor = jwtInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor);
    }
}
