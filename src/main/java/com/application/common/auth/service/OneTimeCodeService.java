package com.application.common.auth.service;

import com.application.common.auth.jwt.OneTimeCodeStore;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


@Service
public class OneTimeCodeService {
    private final Map<String, OneTimeCodeStore> codeStoreMap = new ConcurrentHashMap<>();

    public void storeOneTimeCode(String code, String accessToken ,String refreshToken){
        codeStoreMap.put(code, new OneTimeCodeStore(accessToken, refreshToken));
    }

    public OneTimeCodeStore findByCode(String code){
        return codeStoreMap.get(code);
    }

    public void deleteByCode(String code){
        codeStoreMap.remove(code);
    }

    @Scheduled(fixedRate = 5*60*1000L)
    public void autoDelete(){
        codeStoreMap.entrySet().removeIf(entry -> Duration.between(entry.getValue().getCreateTime(), LocalDateTime.now()).toMinutes() > 5);
    }


}
