package kr.go.togetherschool.tosweb.model;

import static java.util.Locale.ENGLISH;

public enum Gender {
    MALE, FEMALE, NONE;

    public static Gender fromName(String type) {
        return Gender.valueOf(type.toUpperCase(ENGLISH));
    }

}