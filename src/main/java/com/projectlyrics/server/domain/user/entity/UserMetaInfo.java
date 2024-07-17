package com.projectlyrics.server.domain.user.entity;

import com.projectlyrics.server.domain.user.exception.InvalidAgeException;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Year;
import java.util.Objects;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserMetaInfo {

    public static final long MINIMUM_AGE = 14;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private Integer birthYear;

    UserMetaInfo(Gender gender, Integer birthYear) {
        validateAge(birthYear);
        this.gender = gender;
        this.birthYear = birthYear;
    }

    private void validateAge(Integer birthYear) {
        if (Objects.isNull(birthYear)) {
            return;
        }
        Year minusBirthYears = Year.now().minusYears(MINIMUM_AGE);
        if (minusBirthYears.isBefore(Year.of(birthYear))) {
            throw new InvalidAgeException();
        }
    }
}
