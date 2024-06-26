package com.projectlyrics.server.domain.user.entity;

import com.projectlyrics.server.domain.user.exception.InvalidUsernameException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

import static com.projectlyrics.server.domain.common.util.DomainUtils.checkString;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Username {

    private final static Pattern pattern = Pattern.compile("^[a-zA-Z0-9가-힣]{1,10}$");

    @Column(name = "username", nullable = false)
    private String value;

    Username(String value) {
        validateUsername(value);
        this.value = value;
    }

    private void validateUsername(String value) {
        checkString(value);
        if (!pattern.matcher(value).matches()) {
            throw new InvalidUsernameException();
        }
    }
}
