package com.application.domain.enums;

import lombok.Getter;

import java.util.Optional;

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

    public static Optional<Mbti> fromMBTI(String mbti){
        for (Mbti value : Mbti.values()) {
            if(value.getMbti().equalsIgnoreCase(mbti))
                return Optional.of(value);
        }
        return Optional.empty();
    }
}
