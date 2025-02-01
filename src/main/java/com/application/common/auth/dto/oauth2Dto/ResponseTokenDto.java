package com.application.common.auth.dto.oauth2Dto;

import lombok.Getter;

@Getter
public class ResponseTokenDto {
    private String accessToken;
    private String refreshToken;

    public ResponseTokenDto(String accessToken , String refreshToken){
        this.accessToken = "Bearer " + accessToken;
        this.refreshToken = refreshToken;
    }
}
