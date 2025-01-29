package com.application.common.auth.controller;


import com.application.common.auth.dto.oauth2Dto.JWTStoreDto;
import com.application.common.auth.dto.oauth2Dto.ResponseRefreshDto;
import com.application.common.auth.jwt.JWTUtil;
import com.application.common.auth.service.JWTStoreService;
import com.application.common.response.ResponseDto;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;

@Slf4j
@Controller
@ResponseBody
@RequiredArgsConstructor
public class AuthController {

    private final JWTStoreService jwtStoreService;
    private final JWTUtil jwtUtil;


    @PostMapping("/api/auth/refresh")
    public ResponseEntity<?> findJwtToken(HttpServletRequest request){
        String refreshToken = request.getHeader("Refresh-Token");
        log.info("refresh token : " + refreshToken);

        String key = jwtUtil.getUUID(refreshToken);

        if(jwtUtil.isRefreshExpired(refreshToken)){
            return new ResponseEntity<>(new ResponseDto<>(-1, "refresh token expired", ""), HttpStatus.OK);
        }

        //삭제
        jwtStoreService.deleteByKey(key);

        String newAccessToken = jwtUtil.createAccessJwt(jwtUtil.getCredentialId(refreshToken),jwtUtil.getRole(refreshToken) );
        String newRefreshToken = jwtUtil.createRefreshJwt(jwtUtil.getCredentialId(refreshToken),jwtUtil.getRole(refreshToken) );
        //갱신
        jwtStoreService.save(key, newRefreshToken);


        return new ResponseEntity<>(new ResponseDto<>(1, "access token and refersh token reissue.", new ResponseRefreshDto(newAccessToken, newRefreshToken)), HttpStatus.OK);
    }

    //TEST CODE
    @GetMapping("/api/test")
    public String findJwtToken(){
        return "test";
    }

}
