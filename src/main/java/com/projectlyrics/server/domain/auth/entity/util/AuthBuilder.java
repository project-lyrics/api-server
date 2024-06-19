package com.projectlyrics.server.domain.auth.entity.util;

import com.projectlyrics.server.domain.auth.entity.Auth;
import com.projectlyrics.server.domain.auth.entity.enumerate.AuthProvider;
import com.projectlyrics.server.domain.auth.entity.enumerate.Role;

import static com.projectlyrics.server.domain.common.util.DomainUtils.checkEnum;
import static com.projectlyrics.server.domain.common.util.DomainUtils.checkString;

public class AuthBuilder {

    public static Auth build(AuthProvider authProvider, Role role, String socialId) {
        checkEnum(authProvider);
        checkEnum(role);
        checkString(socialId);

        return Auth.builder()
                .authProvider(authProvider)
                .role(role)
                .socialId(socialId)
                .build();
    }
}
