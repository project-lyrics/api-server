package com.projectlyrics.server.common.fixture;

import com.projectlyrics.server.domain.auth.entity.Auth;
import com.projectlyrics.server.domain.auth.entity.enumerate.AuthProvider;
import com.projectlyrics.server.domain.auth.entity.enumerate.Role;
import com.projectlyrics.server.domain.user.entity.User;

public class UserFixture {

    public static User create() {
        return User.of(null, "test@test.com");
    }

    public static User createKakao() {
        return User.of(
                Auth.of(AuthProvider.KAKAO, Role.USER, "socialId"),
                "test@test.com"
        );
    }

    public static User createApple() {
        return User.of(
                Auth.of(AuthProvider.APPLE, Role.USER, "socialId"),
                "test@test.com"
        );
    }
}
