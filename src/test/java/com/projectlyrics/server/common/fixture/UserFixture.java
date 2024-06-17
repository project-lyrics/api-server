package com.projectlyrics.server.common.fixture;

import com.projectlyrics.server.domain.auth.entity.Auth;
import com.projectlyrics.server.domain.auth.entity.enumerate.AuthProvider;
import com.projectlyrics.server.domain.auth.entity.enumerate.Role;
import com.projectlyrics.server.domain.user.entity.User;

public class UserFixture {

  public static User create() {
    return User.builder()
        .email("abcde@gmail.com")
        .build();
  }

  public static User createKakao() {
    return User.builder()
        .email("test@test.com")
        .auth(new Auth("socialId", AuthProvider.KAKAO, Role.USER))
        .build();
  }

  public static User createApple() {
    return User.builder()
        .email("test@test.com")
        .auth(new Auth("socialId", AuthProvider.APPLE, Role.USER))
        .build();
  }
}
