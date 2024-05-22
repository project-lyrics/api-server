package com.projectlyrics.server.domain.artist.dto.response;

import com.projectlyrics.server.domain.artist.entity.Artist;

public record ArtistGetResponse(
    Long id,
    String name,
    String englishName,
    String profileImageCdnLink
) {

  public static ArtistGetResponse from(Artist artist) {
    return new ArtistGetResponse(
        artist.getId(),
        artist.getName(),
        artist.getEnglishName(),
        artist.getProfileImageCdnLink()
    );
  }
}
