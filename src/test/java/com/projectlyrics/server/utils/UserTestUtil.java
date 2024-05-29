package com.projectlyrics.server.utils;

import com.projectlyrics.server.domain.user.entity.User;

public class UserTestUtil {

  public static User create() {
    return User.builder()
        .email("abcde@gmail.com")
        .build();
  }
}
