package com.zad.wallet.interceptor;

import com.zad.wallet.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


@Component
@AllArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {
    private final JwtService jwtService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        var auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Missing or invalid Authorization header");
            return false;
        }

        var token = auth.substring(7);
        if (!jwtService.validateJwtToken(token)) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid or expired token");
            return false;
        }

        var userId = jwtService.getUserIdFromToken(token);
        request.setAttribute("userId", userId);
        return true;
    }
}

