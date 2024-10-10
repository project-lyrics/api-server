package com.projectlyrics.server.domain.user.entity;

import com.projectlyrics.server.domain.user.exception.InvalidUsernameException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.regex.Pattern;

import static com.projectlyrics.server.domain.common.util.DomainUtils.checkString;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Username {

    private final static Pattern pattern = Pattern.compile("^[a-zA-Z0-9가-힣]{1,10}$");

    @Column(name = "nickname")
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Username username = (Username) o;
        return Objects.equals(value, username.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
