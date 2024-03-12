package com.projectlyrics.server.utils;

import com.projectlyrics.server.domain.artist.entity.Artist;

public class ArtistTestUtil {

  public static Artist create() {
    return Artist.builder()
        .name("넬")
        .englishName("NELL")
        .profileImageCdnLink("https://~")
        .build();
  }
}
