package com.application.common.auth.service;

import com.application.common.Constant;
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

@Slf4j
@Component
public class CustomSuccessHandler  implements AuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final JWTStoreService jwtStoreService;

    public CustomSuccessHandler(JWTUtil jwtUtil, JWTStoreService jwtStoreService){
        this.jwtUtil = jwtUtil;
        this.jwtStoreService = jwtStoreService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();
        String credentialId = customUserDetails.getCredentialId();
        String role = getRoleByAuthentication(authentication);

        String accessToken = jwtUtil.createAccessJwt(credentialId, role);
        String refreshToken = jwtUtil.createRefreshJwt(credentialId, role);


        jwtStoreService.save(jwtUtil.getUUID(refreshToken), refreshToken);


        response.setHeader("Authorization", "Bearer " + accessToken);
        response.setHeader("Refresh-Token", refreshToken);


        log.info("uuid: " + jwtUtil.getUUID(refreshToken));
        log.info("jwtToken : " + accessToken);
        log.info("refreshToken : " + jwtStoreService.findByKey(jwtUtil.getUUID(refreshToken)).getRefreshToken());

        sendResponse(accessToken,refreshToken,response);

    }


    private String getRoleByAuthentication(Authentication authentication){
        return authentication.getAuthorities().iterator().next().getAuthority();
    }

    private void sendResponse(String accessToken, String refreshToken, HttpServletResponse response) throws IOException, JsonProcessingException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String responseBody = new ObjectMapper().writeValueAsString(
                Map.of("accessToken", "Bearer "+accessToken, "refreshToken", refreshToken)
        );
        response.getWriter().write(responseBody);
    }

}
