package com.application.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OauthResponseModeConfig {
    @Value("${oauth.mode}")
    private String oauthMode;


    public String isMode(){
        return oauthMode;
    }
}
