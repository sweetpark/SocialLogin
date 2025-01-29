package com.application.common.config;

import com.application.domain.repository.MemMemberRepository;
import com.application.domain.repository.MemberRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public MemberRepository getMemberRepository(){
        return new MemMemberRepository();
    }
}
