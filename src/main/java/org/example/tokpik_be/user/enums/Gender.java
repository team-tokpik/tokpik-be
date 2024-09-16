package org.example.tokpik_be.user.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Gender {

    MALE("남성"),
    FEMALE("여성");

    private final String description;

    public boolean toBoolean() {

        return this == MALE;
    }

    public static Gender from(boolean value) {

        return value ? MALE : FEMALE;
    }
}
