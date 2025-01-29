package com.application.common.auth.dto.oauth2Dto;

import lombok.Getter;

@Getter
public class ResponseRefreshDto {
    private String accessToken;
    private String refreshToken;

    public ResponseRefreshDto(String accessToken , String refreshToken){
        this.accessToken = "Bearer " + accessToken;
        this.refreshToken = refreshToken;
    }
}
