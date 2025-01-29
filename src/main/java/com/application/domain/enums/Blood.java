package com.application.domain.enums;

import lombok.Getter;

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

    public static Blood fromBlood(String blood){
        for (Blood value : Blood.values()) {
            if (value.getBlood().equalsIgnoreCase(blood)){
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown blood type" + blood);
    }
}
