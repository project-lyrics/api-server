package com.projectlyrics.server.domain.artist.dto.response;

import com.projectlyrics.server.domain.artist.entity.Artist;

public record GetArtistResponse(
    Long id,
    String name,
    String englishName,
    String profileImageCdnLink
) {

  public static GetArtistResponse from(Artist artist) {
    return new GetArtistResponse(
        artist.getId(),
        artist.getName(),
        artist.getEnglishName(),
        artist.getProfileImageCdnLink()
    );
  }
}
