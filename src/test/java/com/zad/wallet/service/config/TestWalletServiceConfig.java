package com.zad.wallet.service.config;

import com.zad.wallet.config.WebConfig;
import com.zad.wallet.interceptor.JwtInterceptor;
import com.zad.wallet.service.JwtService;
import com.zad.wallet.service.RateLimiterService;
import com.zad.wallet.service.WalletService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@TestConfiguration
public class TestWalletServiceConfig {
    private final WalletService walletService = Mockito.mock(WalletService.class);

    @Bean
    public RateLimiterService rateLimiterService() {
        return Mockito.mock(RateLimiterService.class);
    }

    @Bean
    public WalletService walletService() {
        return walletService;
    }

    @Bean
    public JwtService jwtService() {
        return Mockito.mock(JwtService.class);
    }

    @Bean
    public JwtInterceptor jwtInterceptor(JwtService jwtService) {
        return new JwtInterceptor(jwtService);
    }

    @Bean
    public WebMvcConfigurer webMvcConfigurer(JwtInterceptor jwtInterceptor) {
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(jwtInterceptor);
            }
        };
    }

    public WalletService getWalletService() {
        return walletService;
    }

}
