package com.application.domain.enums;

import lombok.Getter;

@Getter
public enum Mbti {
    INTP("intp"),
    INTJ("intj"),
    INFP("infp"),
    INFJ("infj"),
    ISTP("istp"),
    ISTJ("istj"),
    ISFP("isfp"),
    ISFJ("isfj"),
    ENTP("entp"),
    ENTJ("entj"),
    ENFP("enfp"),
    ENFJ("enfj"),
    ESTP("estp"),
    ESTJ("estj"),
    ESFP("esfp"),
    ESFJ("esfj");

    private String mbti;

    Mbti(String mbti){
        this.mbti = mbti;
    }

    public Mbti fromMBTI(String mbti){
        for (Mbti value : Mbti.values()) {
            if(value.getMbti().equalsIgnoreCase(mbti))
                return value;
        }
        throw new IllegalArgumentException("No Type MBTI");
    }
}
