package com.projectlyrics.server.common.fixture;

import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.record.domain.Record;
import com.projectlyrics.server.domain.user.entity.User;

public class RecordFixture {

  public static Record create(User user, Artist artist) {
    return Record.builder()
        .user(user)
        .artist(artist)
        .build();
  }
}
