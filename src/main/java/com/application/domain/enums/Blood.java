package com.application.domain.enums;

import lombok.Getter;

import java.util.Optional;

@Getter
public enum Blood {
    A("A"),
    AB("AB"),
    B("B"),
    O("O");

    private String blood;

    Blood(String blood){
        this.blood = blood;
    }

    public static Optional<Blood> fromBlood(String blood){
        for (Blood value : Blood.values()) {
            if (value.getBlood().equalsIgnoreCase(blood)){
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }
}
