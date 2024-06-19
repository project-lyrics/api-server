package com.projectlyrics.server.domain.user.entity.util;

import com.projectlyrics.server.domain.auth.entity.Auth;
import com.projectlyrics.server.domain.user.entity.User;

import static com.projectlyrics.server.domain.common.util.DomainUtils.checkString;

public class UserBuilder {

    public static User build(Auth auth, String email) {
        checkString(email);

        return User.builder()
                .auth(auth)
                .email(email)
                .build();
    }
}
