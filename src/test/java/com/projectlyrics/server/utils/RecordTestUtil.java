package com.projectlyrics.server.utils;

import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.record.domain.Record;
import com.projectlyrics.server.domain.user.entity.User;

public class RecordTestUtil {

  public static Record create(User user, Artist artist) {
    return Record.builder()
        .user(user)
        .artist(artist)
        .build();
  }
}
