package com.projectlyrics.server.domain.user.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserMetaInfo {

    @Enumerated(EnumType.STRING)
    private Gender gender;
    private int birthYear;

    public UserMetaInfo(Gender gender, int birthYear) {
        this.gender = gender;
        this.birthYear = birthYear;
    }
}
