package com.application.domain.entity;

import com.application.domain.enums.*;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
public class Member {
    private Long id;
    private String creadentialId; // social login id
    private String name; // 이름
    private String tendency; //성향
    private Sex sex; //male, female
    private Mbti mbti; // INTP, ENTP ...
    private Blood blood; // A, AB, B, O
    private String addr;
    private School school; // element, middle, high, collehe, graduate
    private String job;
    private Integer age; //나이
    private Role role; // Admin, User, Other
    private Boolean firstSignUp;
    private LocalDateTime createTime;// CHECKME) 직렬화 필요?

    //google example
    private String email;
    private String picture;



    @Builder
    public Member(String name, String email, String picture, Role role){
        this.name = name;
        this.email = email;
        this.picture = picture;
        this.role = role;
    }

    //google example
    public Member update(String name, String picture){
        this.name = name;
        this.picture = picture;

        return this;
    }

    public String getRole(){
        return this.role.getEnglish();
    }


}
