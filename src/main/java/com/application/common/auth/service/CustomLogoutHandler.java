package com.application.common.auth.service;

import com.application.common.auth.jwt.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;




@Slf4j
@Component
public class CustomLogoutHandler implements LogoutHandler {

    private final JWTUtil jwtUtil;
    private final JWTStoreService jwtStoreService;

    public CustomLogoutHandler(JWTUtil jwtUtil, JWTStoreService jwtStoreService){
        this.jwtUtil = jwtUtil;
        this.jwtStoreService = jwtStoreService;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String jwt = getJwtToken(request.getHeader("Authorization"));

        String uuid = jwtUtil.getUUID(jwt);
        log.info("Refresh Token Delete : " + jwtStoreService.findByKey(uuid).getRefreshToken());
        jwtStoreService.deleteByKey(uuid);

    }

    private String  getJwtToken(String token){
        String jwt = token.substring(7);
        return jwt;
    }
}
