package com.application.common.auth.service;

import com.application.common.auth.dto.oauth2Dto.ResponseTokenDto;
import com.application.common.auth.jwt.JWTUtil;
import com.application.domain.entity.Member;
import com.application.domain.enums.Role;
import com.application.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class OAuth2Service {

    private final RestTemplate restTemplate = new RestTemplate();
    private final JWTUtil jwtUtil;
    private final MemberRepository memberRepository;
    private final JWTStoreService jwtStoreService;

    public Map<String, Object> getUserInfoFromGoogle(String accessToken){
        String userInfoEndpoint = "https://www.googleapis.com/oauth2/v1/userinfo?access_token=" + accessToken;
        return restTemplate.getForObject(userInfoEndpoint, Map.class);
    }

    public Map<String, Object> getUserInfoFromNaver(String accessToken){
        String userInfoEndpoint = "https://openapi.naver.com/v1/nid/me";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(userInfoEndpoint, HttpMethod.GET, entity, Map.class);

        return (Map<String,Object>)response.getBody().get("response");
    }


    public ResponseTokenDto getTokenAndSaveMember(String provider, Map<String, Object> userInfo){
        saveOrUpdate(userInfo, provider);

        String uuid = UUID.randomUUID().toString();
        String accessToken = jwtUtil.createAccessJwt(uuid, getCredentialId(userInfo, provider),Role.USER.getEnglish());
        String refreshToken = jwtUtil.createRefreshJwt(uuid, getCredentialId(userInfo, provider), Role.USER.getEnglish());

        jwtStoreService.save(jwtUtil.getUUID(refreshToken), refreshToken);

        log.info("uuid: " + jwtUtil.getUUID(refreshToken));
        log.info("jwtToken : " + accessToken);
        log.info("refreshToken : " + jwtStoreService.findByKey(jwtUtil.getUUID(refreshToken)).getRefreshToken());

        return new ResponseTokenDto(accessToken, refreshToken);
    }

    public void saveOrUpdate(Map<String, Object> userInfo, String provider){
        String creadentialId = getCredentialId(userInfo, provider);
        Member existMember = memberRepository.findByCreadentialId(creadentialId);

        if (existMember == null){
            Member newMember = new Member();

            newMember.setCreadentialId(creadentialId);
            newMember.setName((String)userInfo.get("name"));
            newMember.setEmail((String)userInfo.get("email"));
            newMember.setRole(Role.USER);
            newMember.setFirstSignUp(true); // 나머지 항목들 추가할때 false설정
            newMember.setCreateTime(LocalDateTime.now());

            memberRepository.save(newMember);
        }else{
            existMember.setEmail((String)userInfo.get("email"));
            existMember.setName((String)userInfo.get("name"));
        }
    }

    public String getCredentialId(Map<String, Object>userInfo, String provider){
        if (provider.equalsIgnoreCase("naver")){
            return provider + userInfo.get("id").toString();
        }else if(provider.equalsIgnoreCase("google")){
            return provider + userInfo.get("sub").toString();
        }else{
            return "";
        }
    }

}
