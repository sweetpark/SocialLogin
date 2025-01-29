package com.application.domain.enums;

import lombok.Getter;

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


    public School fromSCHOOL(String school){
        for (School value : School.values()) {
            if(value.getSchool().equalsIgnoreCase(school))
                return value;
        }
        throw new IllegalArgumentException("No Type School");
    }


}
