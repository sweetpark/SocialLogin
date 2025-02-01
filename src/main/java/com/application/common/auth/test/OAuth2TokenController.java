package com.application.common.auth.test;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class OAuth2TokenController {
    private final OAuth2TokenService oAuth2TokenService;

    public OAuth2TokenController(OAuth2TokenService oAuth2TokenService) {
        this.oAuth2TokenService = oAuth2TokenService;
    }

    // 네이버 AccessToken 요청
    @GetMapping("/login/oauth2/code/naver")
    public ResponseEntity<Map> getNaverAccessToken(@RequestParam("code") String code,
                                                                   @RequestParam("state") String state) {
        Map<String, Object> tokenResponse = oAuth2TokenService.getNaverAccessToken(code, state);

        String accessToken = (String) tokenResponse.get("access_token");
        ResponseEntity<Map> authResponse = oAuth2TokenService.sendAccessTokenToAuthService(accessToken);

        return authResponse;
    }

    // 구글 AccessToken 요청
    @GetMapping("/login/oauth2/code/google")
    public ResponseEntity<Map<String, Object>> getGoogleAccessToken(@RequestParam("code") String code) {
        Map<String, Object> tokenResponse = oAuth2TokenService.getGoogleAccessToken(code);
        return ResponseEntity.ok(tokenResponse);
    }
}
