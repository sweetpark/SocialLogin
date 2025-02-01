package com.application.common.auth.controller;


import com.application.common.auth.dto.oauth2Dto.JWTStoreDto;
import com.application.common.auth.dto.oauth2Dto.ResponseRefreshDto;
import com.application.common.auth.jwt.JWTUtil;
import com.application.common.auth.jwt.OneTimeCodeStore;
import com.application.common.auth.service.JWTStoreService;
import com.application.common.auth.service.OneTimeCodeService;
import com.application.common.response.ResponseDto;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Controller
@ResponseBody
@RequiredArgsConstructor
public class AuthController {

    private final JWTStoreService jwtStoreService;
    private final JWTUtil jwtUtil;
    private final OneTimeCodeService oneTimeCodeService;


    @PostMapping("/api/auth/refresh")
    public ResponseEntity<?> findJwtToken(HttpServletRequest request){
        String refreshToken = request.getHeader("Refresh-Token");
        log.info("refresh token : " + refreshToken);

        String key = jwtUtil.getUUID(refreshToken);


        if(jwtUtil.isRefreshExpired(refreshToken) || !jwtStoreService.containKey(key)){
            return new ResponseEntity<>(new ResponseDto<>(-1, "refresh token expired", ""), HttpStatus.OK);
        }

        //삭제
        jwtStoreService.deleteByKey(key);

        String uuid = UUID.randomUUID().toString();
        String newAccessToken = jwtUtil.createAccessJwt(uuid, jwtUtil.getCredentialId(refreshToken),jwtUtil.getRole(refreshToken) );
        String newRefreshToken = jwtUtil.createRefreshJwt(uuid, jwtUtil.getCredentialId(refreshToken),jwtUtil.getRole(refreshToken) );
        //갱신
        jwtStoreService.save(key, newRefreshToken);


        return new ResponseEntity<>(new ResponseDto<>(1, "access token and refersh token reissue.", new ResponseRefreshDto(newAccessToken, newRefreshToken)), HttpStatus.OK);
    }

    @PostMapping("/api/exchange/token")
    public ResponseEntity<?> exchangeToken(@RequestBody Map<String, String> request){
        String uuid = request.get("code");

        OneTimeCodeStore oneTimeCodeStore = oneTimeCodeService.findByCode(uuid);

        if(oneTimeCodeStore == null){
            return new ResponseEntity<>(new ResponseDto<>(-1, "invalid code or expired code", null), HttpStatus.UNAUTHORIZED);
        }

        ResponseRefreshDto responseRefreshDto = new ResponseRefreshDto(oneTimeCodeStore.getAccessToken(), oneTimeCodeStore.getRefreshToken());
        oneTimeCodeService.deleteByCode(uuid);
        return new ResponseEntity<>(new ResponseDto<>(1, "access token and refresh token", responseRefreshDto),HttpStatus.OK);
    }


    @GetMapping("/api/redirect/test")
    public ResponseEntity<String> findJwtToken(@RequestParam("code") String code){
        RestTemplate restTemplate = new RestTemplate();

        String exchangeUrl = "http://localhost:8080/api/exchange/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(Map.of("code", code), headers);

        // POST 요청 실행
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                exchangeUrl, HttpMethod.POST, requestEntity, String.class);

        // 응답 반환
        return responseEntity;
    }

    //TEST CODE
    @GetMapping("/api/test")
    public String findJwtToken(){
        return "test";
    }

}
