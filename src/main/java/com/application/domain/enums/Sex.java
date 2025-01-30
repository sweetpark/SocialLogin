package com.application.domain.enums;

import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Getter
public enum Sex {
    MALE("male"),
    FEMALE("female");

    private String english;

    Sex(String eng){
        this.english = eng;
    }



    public static Optional<Sex> fromString(String sex){
        for (Sex value : Sex.values()) {
            if(value.getEnglish().equalsIgnoreCase(sex))
                return Optional.of(value);
        }
        return Optional.empty();
    }

}
