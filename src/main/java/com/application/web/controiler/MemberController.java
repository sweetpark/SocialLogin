package com.application.web.controiler;

import com.application.common.auth.dto.oauth2Dto.CustomOAuth2User;
import com.application.common.response.ResponseDto;
import com.application.domain.dto.MemberInitDto;
import com.application.domain.entity.Member;
import com.application.domain.enums.Blood;
import com.application.domain.enums.Mbti;
import com.application.domain.enums.School;
import com.application.domain.enums.Sex;
import com.application.domain.repository.MemberRepository;
import com.application.web.services.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("/api")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/get/member")
    public ResponseEntity<?> getMember(@AuthenticationPrincipal CustomOAuth2User customOAuth2User){
        Member member = memberService.getMemberByCredentialId(customOAuth2User.getCredentialId());
        return new ResponseEntity<>(new ResponseDto<>(1, "Get Member Info", member), HttpStatus.OK);
    }

    @PostMapping("/update/member")
    public ResponseEntity<?> updateMember(@AuthenticationPrincipal CustomOAuth2User customOAuth2User, @RequestBody MemberInitDto setInitMember){
        Member member = memberService.getMemberByCredentialId(customOAuth2User.getCredentialId());
        //필요하면 ResponseDto 만들기
        Member updateMember = memberService.updateMemberInfo(member, setInitMember);

        return new ResponseEntity<>(new ResponseDto<>(1, "Set First Join Member Info", updateMember), HttpStatus.OK);
    }

    @DeleteMapping("/delete/member")
    public ResponseEntity<?> deleteMember(@AuthenticationPrincipal CustomOAuth2User customOAuth2User){
        Long memberId = memberService.getMemberByCredentialId(customOAuth2User.getCredentialId()).getId();
        memberService.deleteMember(memberId);
        return new ResponseEntity<>(new ResponseDto<>(1, "Delete Member", null), HttpStatus.OK);
    }


}
