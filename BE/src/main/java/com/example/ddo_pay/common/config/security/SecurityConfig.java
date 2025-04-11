package com.example.ddo_pay.common.config.security;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.ddo_pay.common.util.JwtUtil;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        // 인증 등록 부분
        @Bean
        public JwtUtil jwtUtil() {
                return new JwtUtil();
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http, JwtUtil jwtUtil) throws Exception {
                http
                    .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                    .csrf(csrf -> csrf.disable())
                    .httpBasic(httpBasic -> httpBasic.disable())
                    .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/kakao/callback").permitAll()
                        .requestMatchers("/api/pay/pos").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().permitAll()
                    )
                    .addFilterBefore(new CustomAuthenticationFilter(jwtUtil),
                        UsernamePasswordAuthenticationFilter.class);
                return http.build();
        }

        // Cors 설정 부분
        // 변경 부분: 빈 이름을 "corsConfigurationSource"로 변경해야 Spring Security가 자동 인식합니다.

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration corsconfig = new CorsConfiguration();
                corsconfig.setAllowedOrigins(
                Arrays.asList("http://j12e106.p.ssafy.io",
                "http://j12e106.p.ssafy.io:3000",
                "https://j12e106.p.ssafy.io",
                "https://j12e106.p.ssafy.io:3000",
                "http://localhost:3000"));
                corsconfig.setAllowedOriginPatterns(Collections.singletonList("*"));
                corsconfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                corsconfig.setAllowedHeaders(Collections.singletonList("*"));
                corsconfig.setAllowCredentials(true);

                // 엔드포인트 설정
                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", corsconfig);
                return source;
        }

}
