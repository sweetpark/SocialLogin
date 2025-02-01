package com.application.common.auth.jwt;

import lombok.Getter;


import java.time.LocalDateTime;


@Getter
public class OneTimeCodeStore {
     private String accessToken;
     private String refreshToken;
     private LocalDateTime createTime;

     public OneTimeCodeStore(String accessToken, String refreshToken){
         this.accessToken = accessToken;
         this.refreshToken = refreshToken;
         this.createTime = LocalDateTime.now();
     }

}
