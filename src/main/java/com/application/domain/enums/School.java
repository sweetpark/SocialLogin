package com.application.domain.enums;

import lombok.Getter;

import java.util.Optional;

@Getter
public enum School {
    ELEMENTARYSCHOOL("elementSchool"),
    MIDDLESCHOOL("middleSchool"),
    HIGHSCHOOL("highSchool"),
    COLLEGE("college"),
    GRADUATESCHOOL("graduateSchool");

    private String school;

    School(String school){
        this.school = school;
    }


    public static Optional<School> fromSCHOOL(String school){
        for (School value : School.values()) {
            if(value.getSchool().equalsIgnoreCase(school))
                return Optional.of(value);
        }
        return Optional.empty();
    }


}
