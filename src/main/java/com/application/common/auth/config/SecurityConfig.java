package com.application.common.auth.config;

import com.application.common.auth.service.CustomFailureHandler;
import com.application.common.auth.service.CustomSuccessHandler;
import com.application.common.auth.jwt.JWTFilter;
import com.application.common.auth.jwt.JWTUtil;
import com.application.common.auth.service.CustomOAuth2UserService;
import com.application.common.auth.service.JWTStoreService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity(debug = false)
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomSuccessHandler customSuccessHandler;
    private final CustomFailureHandler customFailureHandler;
    private final JWTStoreService jwtStoreService;
    private final JWTUtil jwtUtil;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService,
                          CustomSuccessHandler customSuccessHandler,
                          CustomFailureHandler customFailureHandler,
                          JWTStoreService jwtStoreService,
                          JWTUtil jwtUtil){
        this.customOAuth2UserService = customOAuth2UserService;
        this.customSuccessHandler = customSuccessHandler;
        this.customFailureHandler = customFailureHandler;
        this.jwtStoreService = jwtStoreService;
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
                .addFilterBefore(new JWTFilter(jwtUtil, jwtStoreService), OAuth2AuthorizationRequestRedirectFilter.class);

        http
                .oauth2Login((oauth2) -> oauth2
                        .userInfoEndpoint( (userInfo -> userInfo.userService(customOAuth2UserService)))
                        .successHandler(customSuccessHandler)
                        .failureHandler(customFailureHandler));

        http
                .authorizeHttpRequests((auth)->auth
                        .requestMatchers("/", "/api/auth/refresh" ).permitAll()
                        .anyRequest().authenticated());

        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));


        return http.build();
    }

}
