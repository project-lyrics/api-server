package com.projectlyrics.server.domain.artist.dto.response;

import com.projectlyrics.server.domain.artist.entity.Artist;

public record UpdateArtistResponse(
    Long id,
    String name,
    String englishName,
    String profileImageCdnLink
) {

  public static UpdateArtistResponse from(Artist artist) {
    return new UpdateArtistResponse(
        artist.getId(),
        artist.getName(),
        artist.getEnglishName(),
        artist.getProfileImageCdnLink()
    );
  }
}
