package com.projectlyrics.server.domain.auth.authentication;

import com.projectlyrics.server.domain.auth.exception.InvalidTokenException;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Objects;

@Getter
@RequestScope
@Component
public class AuthContext {

    private String nickname;
    private long id;

    public void setNickname(String nickname) {
        if (Objects.isNull(nickname)) {
            throw new InvalidTokenException();
        }
        this.nickname = nickname;
    }

    public void setId(long id) {
        this.id = id;
    }
}
