package com.projectlyrics.server.fixture;

import com.projectlyrics.server.domain.artist.entity.Artist;

public class ArtistFixture {

  public static Artist create() {
    return Artist.builder()
        .name("넬")
        .englishName("NELL")
        .profileImageCdnLink("https://~")
        .build();
  }

  public static Artist createWithName(String name) {
    return Artist.builder()
        .name(name)
        .build();
  }
}
