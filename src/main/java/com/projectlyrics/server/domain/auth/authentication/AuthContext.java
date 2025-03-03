package com.projectlyrics.server.domain.auth.authentication;

import com.projectlyrics.server.domain.auth.exception.InvalidTokenException;
import com.projectlyrics.server.domain.user.entity.Role;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Objects;

@Setter
@Getter
@RequestScope
@Component
public class AuthContext {

    private String nickname;
    @Setter
    private long id;
    @Setter
    private Role role;

    public void setNickname(String nickname) {
        if (Objects.isNull(nickname)) {
            throw new InvalidTokenException();
        }
        this.nickname = nickname;
    }

    public boolean isAnonymous() {
        return Objects.isNull(nickname);
    }
}
