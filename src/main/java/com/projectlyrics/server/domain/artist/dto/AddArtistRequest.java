package com.projectlyrics.server.domain.artist.dto;

import com.projectlyrics.server.domain.artist.entity.Artist;

public record AddArtistRequest(
    String name,
    String englishName,
    String profileImageCdnLink
) {

  public Artist toEntity() {
    return Artist.builder()
        .name(this.name)
        .englishName(this.englishName)
        .profileImageCdnLink(this.profileImageCdnLink)
        .build();
  }
}
