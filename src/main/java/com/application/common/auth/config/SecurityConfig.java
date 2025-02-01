package com.application.common.auth.config;

import com.application.common.auth.service.*;
import com.application.common.auth.jwt.JWTFilter;
import com.application.common.auth.jwt.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;


@Configuration
@EnableWebSecurity(debug = false)
public class SecurityConfig {


    private final CustomLogoutHandler customLogoutHandler;
    private final JWTUtil jwtUtil;

    public SecurityConfig(CustomLogoutHandler customLogoutHandler,
                          JWTUtil jwtUtil){
        this.customLogoutHandler = customLogoutHandler;
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        http
                .csrf((auth) -> auth.disable());

        http
                .formLogin((auth) -> auth.disable());

        http
                .httpBasic((auth) -> auth.disable());

        http
                .addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);


        http
                .authorizeHttpRequests((auth)->auth
                        .requestMatchers("/api/auth/refresh", "/api/auth/social-login").permitAll()
                        .requestMatchers("/api/auth/naver/token", "/api/auth/google/token", "/api/auth/**", "login/**").permitAll()
                        .anyRequest().authenticated());

        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http
                .logout((auth) -> auth
                        .logoutUrl("/logout")
                        .addLogoutHandler(customLogoutHandler)
                        .logoutSuccessHandler(((request, response, authentication) ->{
                            response.setHeader("Authorization", "");
                            response.setHeader("Refresh-Token","");
                            response.setContentType("application/json");
                            response.setCharacterEncoding("UTF-8");
                            response.getWriter().write("Success logout");
                            response.flushBuffer();}
                        )));

        return http.build();
    }

}
