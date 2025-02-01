package com.application.common.auth.test;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class OAuth2TokenService {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String naverClientId;

    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String naverClientSecret;

    @Value("${spring.security.oauth2.client.registration.naver.redirect-uri}")
    private String naverRedirectUri;

    @Value("${spring.security.oauth2.client.provider.naver.token-uri}")
    private String naverTokenUri;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String googleRedirectUri;

    @Value("${spring.security.oauth2.client.provider.google.token-uri}")
    private String googleTokenUri;

    // 네이버 AccessToken 요청
    public Map<String, Object> getNaverAccessToken(String authorizationCode, String state) {
        return getAccessToken(naverTokenUri, naverClientId, naverClientSecret, naverRedirectUri, authorizationCode, state);
    }

    // 구글 AccessToken 요청
    public Map<String, Object> getGoogleAccessToken(String authorizationCode) {
        return getAccessToken(googleTokenUri, googleClientId, googleClientSecret, googleRedirectUri, authorizationCode, null);
    }

    // 공통 AccessToken 요청 메서드
    public Map<String, Object> getAccessToken(String tokenUri, String clientId, String clientSecret, String redirectUri, String code, String state) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED); // ✅ Content-Type 설정

        // ✅ MultiValueMap 사용
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);
        if (state != null) {
            body.add("state", state);
        }

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(tokenUri, HttpMethod.POST, requestEntity, Map.class);

        return response.getBody();
    }

    public ResponseEntity<Map> sendAccessTokenToAuthService(String accessToken) {
        String socialLoginUrl = "http://localhost:8080/api/auth/social-login";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // JSON 형식의 요청 본문 생성
        Map<String, String> body = new HashMap<>();
        body.put("provider", "naver");
        body.put("accessToken", accessToken);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(body, headers);
        return restTemplate.exchange(socialLoginUrl, HttpMethod.POST, requestEntity, Map.class);
    }
}
