package com.ll.exam.sb_jwt_exam.app.security;

import com.ll.exam.sb_jwt_exam.app.security.filter.JwtAuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthorizationFilter jwtAuthorizationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationConfiguration authenticationConfiguration) throws Exception {
        http
                .authorizeRequests( // /member/login과 /member/join 제외 모든 요청은 로그인 후 사용할 수 있도록
                        authorizeRequests -> authorizeRequests
                                .antMatchers("/member/login", "/member/join")
                                .permitAll()
                                .anyRequest()
                                .authenticated() // 최소자격 : 로그인
                )
                .cors().disable() // 타 도메인에서 API 호출 가능
                .csrf().disable() // CSRF 토큰 끄기
                .httpBasic().disable() // httpBaic 로그인 방식 끄기
                .formLogin().disable() // 폼 로그인 방식 끄기
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(STATELESS)
                ).addFilterBefore(
                        jwtAuthorizationFilter, // JWT 관련 커스텀 필터 // 기존에 있던 UsernamePasswordAuthenticationFilter.class 전에 실행되도록 필터 추가
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}
