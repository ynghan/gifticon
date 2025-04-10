package com.example.ddo_pay.common.config.security;

import java.io.IOException;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.ddo_pay.common.config.security.token.CustomAuthToken;
import com.example.ddo_pay.common.config.security.token.CustomUserDetails;
import com.example.ddo_pay.common.util.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    // JwtUtil을 생성자 주입 받음
    public CustomAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        log.info("CustomAuthenticationFilter Start");
        // OPTIONS 요청은 CORS 예비 요청으로 필터를 건너뛰게 처리
        // if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
        //     log.info("Preflight (OPTIONS) request detected. Skipping authenticationfilter.");
        //     response.setStatus(HttpServletResponse.SC_OK); // 200 OK 응답
        //     filterChain.doFilter(request, response); // 필터 체인 계속 진행
        //     return; // 이후 로직은 실행하지 않음
        // }

        // 예외 URL 처리 (예: /api/users/social/kakao/login)
        if (!request.getRequestURI().equals("/api/users/social/kakao/login") &&
            !request.getRequestURI().equals("/api/auth/kakao/callback")) {
            // "Authorization" 헤더에서 JWT 토큰 추출 (형식: "Bearer <token>")
            String authHeader = request.getHeader("xx-auth");
            log.info("authHeader : " + authHeader);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                log.info("token : " + token);
                if (jwtUtil.validateToken(token)) {
                    Long userId = jwtUtil.getUserIdFromToken(token);
                    // 여기에서 데이터베이스에서 사용자 정보를 로드하거나,
                    // 단순히 userId만 포함하는 CustomUserDetails를 생성할 수 있음
                    CustomUserDetails userDetails = new CustomUserDetails(userId);
                    CustomAuthToken authToken = new CustomAuthToken(
                        userDetails,
                        token,
                        userDetails.getAuthorities()
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
                    return;
                }
            } else {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authorization header missing");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}



