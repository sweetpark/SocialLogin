package com.application.common.auth.test;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OAuth2LoginUrlService {

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String naverClientId;

    @Value("${spring.security.oauth2.client.registration.naver.redirect-uri}")
    private String naverRedirectUri;

    @Value("${spring.security.oauth2.client.provider.naver.authorization-uri}")
    private String naverAuthUri;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String googleRedirectUri;

    @Value("${spring.security.oauth2.client.provider.google.authorization-uri}")
    private String googleAuthUri;

    // 네이버 로그인 URL 생성
    public String getNaverLoginUrl() {
        String state = UUID.randomUUID().toString(); // CSRF 방지를 위한 state 값
        return String.format("%s?response_type=code&client_id=%s&redirect_uri=%s&state=%s",
                naverAuthUri, naverClientId, naverRedirectUri, state);
    }

    // 구글 로그인 URL 생성
    public String getGoogleLoginUrl() {
        return String.format("%s?client_id=%s&redirect_uri=%s&response_type=code&scope=email profile",
                googleAuthUri, googleClientId, googleRedirectUri);
    }
}
