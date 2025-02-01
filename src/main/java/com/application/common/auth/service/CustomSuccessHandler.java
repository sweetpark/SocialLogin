package com.application.common.auth.service;


import com.application.common.Constant;
import com.application.common.OauthResponseModeConfig;
import jakarta.servlet.http.Cookie;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.web.reactive.function.client.WebClient;
import com.application.common.auth.dto.oauth2Dto.CustomOAuth2User;
import com.application.common.auth.jwt.JWTUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class CustomSuccessHandler  implements AuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final JWTStoreService jwtStoreService;
    private final OauthResponseModeConfig oauthResponseModeConfig;
    private final OneTimeCodeService oneTimeCodeService;

    public CustomSuccessHandler(JWTUtil jwtUtil, JWTStoreService jwtStoreService, OauthResponseModeConfig oauthResponseModeConfig, OneTimeCodeService oneTimeCodeService){
        this.jwtUtil = jwtUtil;
        this.jwtStoreService = jwtStoreService;
        this.oauthResponseModeConfig = oauthResponseModeConfig;
        this.oneTimeCodeService = oneTimeCodeService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();
        String credentialId = customUserDetails.getCredentialId();
        String role = getRoleByAuthentication(authentication);

        String uuid = UUID.randomUUID().toString();
        String accessToken = jwtUtil.createAccessJwt(uuid, credentialId, role);
        String refreshToken = jwtUtil.createRefreshJwt(uuid, credentialId, role);


        jwtStoreService.save(jwtUtil.getUUID(refreshToken), refreshToken);

        log.info("uuid: " + jwtUtil.getUUID(refreshToken));
        log.info("jwtToken : " + accessToken);
        log.info("refreshToken : " + jwtStoreService.findByKey(jwtUtil.getUUID(refreshToken)).getRefreshToken());


        if(oauthResponseModeConfig.isMode().equalsIgnoreCase("json")){
            sendResponse(accessToken,refreshToken,response);
        }else if(oauthResponseModeConfig.isMode().equalsIgnoreCase("post")){
            sendTokensToClient(accessToken, refreshToken);
            response.sendRedirect(Constant.FRONT_SEND_REDIRECT);
        }else if(oauthResponseModeConfig.isMode().equalsIgnoreCase("cookie")){
            sendCookie(accessToken, refreshToken, response);
            response.sendRedirect(Constant.FRONT_SEND_REDIRECT);
        }else if (oauthResponseModeConfig.isMode().equalsIgnoreCase("redirect")){
            String code = UUID.randomUUID().toString();
            oneTimeCodeService.storeOneTimeCode(code, accessToken, refreshToken);
            response.sendRedirect(redirectUri(code));
        }else{
            sendResponse(accessToken,refreshToken,response);
        }

    }


    private String getRoleByAuthentication(Authentication authentication){
        return authentication.getAuthorities().iterator().next().getAuthority();
    }

    //json으로 보기
    private void sendResponse(String accessToken, String refreshToken, HttpServletResponse response) throws IOException, JsonProcessingException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String responseBody = new ObjectMapper().writeValueAsString(
                Map.of("accessToken", "Bearer "+accessToken, "refreshToken", refreshToken)
        );
        response.getWriter().write(responseBody);
    }


    //특정 URI로 콜백
    private void sendTokensToClient(String accessToken, String refreshToken){
        WebClient webClient = WebClient.builder()
                .baseUrl(Constant.POST_CLIENT_TOKEN_CALLBACK_URI)
                .defaultHeader("Authorization", "Bearer "+ accessToken)
                .defaultHeader("Refresh-Token", refreshToken)
                .build();

        webClient.post()
                .bodyValue(Map.of("accessToken", "Bearer " +accessToken, "refreshToken", refreshToken))
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(response -> log.info("Response " + response));
    }

    //cookie 사용
    private void sendCookie(String accessToken, String refreshToken, HttpServletResponse response){
        Cookie accessTokenCookie = new Cookie("accessToken", "Bearer " +accessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge((int) TimeUnit.HOURS.toSeconds(1));

        Cookie refreshTokenCookie = new Cookie("refreshToken" , refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge((int) TimeUnit.HOURS.toSeconds(1));

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
    }

    //redirect 사용
    private String redirectUri (String code){
        return Constant.FRONT_OAUTH_REDIRECT_CODE_URI + code;
    }
}
