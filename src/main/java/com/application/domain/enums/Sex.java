package com.application.domain.enums;

import lombok.Getter;

@Getter
public enum Sex {
    MALE("male"),
    FEMALE("female");

    private String english;

    Sex(String eng){
        this.english = eng;
    }



    public Sex fromSEX(String sex){
        for (Sex value : Sex.values()) {
            if(value.getEnglish().equalsIgnoreCase(sex))
                return value;
        }

        throw new IllegalArgumentException("No Type Sex");
    }
}
