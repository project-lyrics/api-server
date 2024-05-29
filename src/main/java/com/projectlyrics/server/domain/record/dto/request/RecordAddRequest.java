package com.projectlyrics.server.domain.record.dto.request;

import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.record.domain.Record;
import com.projectlyrics.server.domain.user.entity.User;

public record RecordAddRequest(
    Long artistId
) {
  public Record toEntity(Artist artist, User user) {
    return Record.builder()
        .artist(artist)
        .user(user)
        .build();
  }
}
