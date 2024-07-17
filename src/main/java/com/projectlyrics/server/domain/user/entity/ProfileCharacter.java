package com.projectlyrics.server.domain.user.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.projectlyrics.server.domain.user.exception.InvalidProfileCharacterException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
public enum ProfileCharacter {

    SHORT_HAIR("shortHair"),
    BRAIDED_HAIR("braidedHair"),
    PARTED_HAIR("partedHair"),
    POOP_HAIR("poopHair");

    private final String type;

    @JsonValue
    public String getType() {
        return type;
    }

    @JsonCreator
    public static ProfileCharacter of(String type) {
        return Arrays.stream(ProfileCharacter.values())
                .filter(profileCharacter -> profileCharacter.type.equals(type))
                .findFirst()
                .orElseThrow(InvalidProfileCharacterException::new);
    }
}
