package com.projectlyrics.server.domain.artist.application.dto;

import com.projectlyrics.server.domain.artist.entity.ArtistEntity;

public class GetSingleArtistMetaDto {

  public record Response(
      Long id,
      String name,
      String englishName,
      String profileImageCdnLink
  ) {

    public static Response from(ArtistEntity artist) {
      return new Response(
          artist.getId(),
          artist.getName(),
          artist.getEnglishName(),
          artist.getProfileImageCdnLink()
      );
    }
  }
}
