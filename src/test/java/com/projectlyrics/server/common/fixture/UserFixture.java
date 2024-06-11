package com.projectlyrics.server.common.fixture;

import com.projectlyrics.server.domain.user.entity.User;

public class UserFixture {

  public static User create() {
    return User.builder()
        .email("abcde@gmail.com")
        .build();
  }
}
