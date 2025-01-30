package com.application.web.services;

import com.application.domain.dto.MemberInitDto;
import com.application.domain.entity.Member;
import com.application.domain.enums.Blood;
import com.application.domain.enums.Mbti;
import com.application.domain.enums.School;
import com.application.domain.enums.Sex;
import com.application.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public Member getMemberByCredentialId(String credentialId){
        return memberRepository.findByCreadentialId(credentialId);
    }

    public Member updateMemberInfo(Member member, MemberInitDto setInitMember){

        if(member.getFirstSignUp()){
            member.setFirstSignUp(false);
        }

        member.setTendency(setInitMember.getTendency());
        member.setSex(Sex.fromString(setInitMember.getSex())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Sex Type")));
        member.setMbti(Mbti.fromMBTI(setInitMember.getMbti())
                .orElseThrow(()-> new IllegalArgumentException("Invalid Mbti Type")));
        member.setBlood(Blood.fromBlood(setInitMember.getBlood())
                .orElseThrow(()-> new IllegalArgumentException("Invalid Blood Type")));
        member.setAddr(setInitMember.getAddr());
        member.setSchool(School.fromSCHOOL(setInitMember.getSchool())
                .orElseThrow(() -> new IllegalArgumentException("Invalid School Type")));
        member.setJob(setInitMember.getJob());
        member.setAge(setInitMember.getAge());
        member.setPicture(setInitMember.getImage());

        //업데이트문
        memberRepository.update(member);

        return member;
    }

    public void deleteMember(Long memberId){
        memberRepository.deleteById(memberId);
    }




}
