package com.projectlyrics.server.domain.artist.dto;

import com.projectlyrics.server.domain.artist.entity.Artist;

public record ArtistDto(
    Long id,
    String name,
    String englishName,
    String profileImageCdnLink
) {

  public static ArtistDto from(Artist artist) {
    return new ArtistDto(
        artist.getId(),
        artist.getName(),
        artist.getEnglishName(),
        artist.getProfileImageCdnLink()
    );
  }
}
