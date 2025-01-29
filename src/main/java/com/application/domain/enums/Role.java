package com.application.domain.enums;

import lombok.Getter;

@Getter
public enum Role {
    ADMIN("ROLE_ADMIN"),
    USER ("ROLE_USER"),
    OTHER("ROLE_OTHER");

    private String english;


    Role(String eng){
        this.english = eng;
    }


    public Role fromROLE(String role){
        for (Role value : Role.values()) {
            if(value.getEnglish().equalsIgnoreCase(role))
                return value;
        }
        throw new IllegalArgumentException("No Type Role");
    }
}
