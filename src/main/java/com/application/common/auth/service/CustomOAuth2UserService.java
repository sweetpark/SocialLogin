package com.application.common.auth.service;

import com.application.common.auth.dto.oauth2Dto.CustomOAuth2User;
import com.application.common.auth.dto.providerResponse.GoogleResponse;
import com.application.common.auth.dto.providerResponse.NaverResponse;
import com.application.common.auth.dto.providerResponse.OAuth2Response;
import com.application.common.auth.dto.oauth2Dto.UserDTO;
import com.application.domain.entity.Member;
import com.application.domain.enums.Role;
import com.application.domain.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    public CustomOAuth2UserService(MemberRepository memberRepository){
        this.memberRepository = memberRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2Response oAuth2Response = providerCheck(userRequest);
        saveOrUpdate(oAuth2Response);

        return new CustomOAuth2User(new UserDTO(oAuth2Response.getProvider() +"-" + oAuth2Response.getProviderId(),
                oAuth2Response.getEmail(),
                oAuth2Response.getName(),
                Role.USER.getEnglish()));

    }


    private OAuth2Response providerCheck(OAuth2UserRequest userRequest){

        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("oAuth2User : " + oAuth2User);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        if(registrationId.equalsIgnoreCase("naver")){
            return new NaverResponse(oAuth2User.getAttributes());
        }else if(registrationId.equalsIgnoreCase("google")){
            return new GoogleResponse(oAuth2User.getAttributes());
        }else{
            return null;
        }
    }



    private void saveOrUpdate(OAuth2Response oAuth2Response){
        String creadentialId = oAuth2Response.getProvider() +"-" + oAuth2Response.getProviderId();
        Member existMember = memberRepository.findByCreadentialId(creadentialId);

        if (existMember == null){
            Member newMember = new Member();

            newMember.setCreadentialId(creadentialId);
            newMember.setName(oAuth2Response.getName());
            newMember.setEmail(oAuth2Response.getEmail());
            newMember.setRole(Role.USER);
            newMember.setFirstSignUp(true); // 나머지 항목들 추가할때 false설정
            newMember.setCreateTime(LocalDateTime.now());

            memberRepository.save(newMember);
        }else{
            existMember.setEmail(oAuth2Response.getEmail());
            existMember.setName(oAuth2Response.getName());
        }
    }
}
