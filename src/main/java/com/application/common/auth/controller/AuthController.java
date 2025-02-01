package com.application.common.auth.controller;


import com.application.common.auth.dto.oauth2Dto.ResponseTokenDto;
import com.application.common.auth.jwt.JWTUtil;
import com.application.common.auth.service.JWTStoreService;
import com.application.common.auth.service.OAuth2Service;
import com.application.common.response.ResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Controller
@ResponseBody
@RequiredArgsConstructor
public class AuthController {

    private final JWTStoreService jwtStoreService;
    private final JWTUtil jwtUtil;
    private final OAuth2Service oAuth2Service;


    @PostMapping("/api/auth/refresh")
    public ResponseEntity<?> findJwtToken(HttpServletRequest request){
        String refreshToken = request.getHeader("Refresh-Token");
        log.info("refresh token : " + refreshToken);

        String key = jwtUtil.getUUID(refreshToken);
        log.info("key: " + key);

        if(jwtUtil.isRefreshExpired(refreshToken) || !jwtStoreService.containKey(key)){
            return new ResponseEntity<>(new ResponseDto<>(-1, "refresh token expired", ""), HttpStatus.OK);
        }

        //삭제
        jwtStoreService.deleteByKey(key);

        String uuid = UUID.randomUUID().toString();
        String newAccessToken = jwtUtil.createAccessJwt(jwtUtil.getCredentialId(refreshToken),jwtUtil.getRole(refreshToken) );
        String newRefreshToken = jwtUtil.createRefreshJwt(uuid, jwtUtil.getCredentialId(refreshToken),jwtUtil.getRole(refreshToken) );
        log.info("new Refresh Key : " + newRefreshToken);

        //갱신
        jwtStoreService.save(uuid, newRefreshToken);


        return new ResponseEntity<>(new ResponseDto<>(1, "access token and refersh token reissue.", new ResponseTokenDto(newAccessToken, newRefreshToken)), HttpStatus.OK);
    }

    @PostMapping("/api/auth/social-login")
    public ResponseEntity<?> socialLogin(@RequestBody Map<String, String> request){
        String provider = request.get("provider");
        String accessToken = request.get("accessToken");
        Map<String, Object> userInfo;

        if("google".equalsIgnoreCase(provider)){
            userInfo = oAuth2Service.getUserInfoFromGoogle(accessToken);
        }else if("naver".equalsIgnoreCase(provider)){
            userInfo = oAuth2Service.getUserInfoFromNaver(accessToken);
        }else{
            return new ResponseEntity<>(new ResponseDto<>(-1, "invalid provider", null), HttpStatus.BAD_REQUEST);
        }

        if(userInfo == null || userInfo.isEmpty()){
            return new ResponseEntity<>(new ResponseDto<>(-1, "invalid access token", null), HttpStatus.UNAUTHORIZED);
        }


        ResponseTokenDto responseTokenDto = oAuth2Service.getToken(provider, userInfo);

        return new ResponseEntity<>(new ResponseDto<>(1, "access token , refresh token create", responseTokenDto), HttpStatus.CREATED);
    }



    //TEST CODE
    @GetMapping("/api/test")
    public String findJwtToken(){
        return "test";
    }

}
