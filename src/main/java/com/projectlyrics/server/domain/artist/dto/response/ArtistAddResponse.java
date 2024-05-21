package com.projectlyrics.server.domain.artist.dto.response;

public record ArtistAddResponse(
    Long id
) {

  public static ArtistAddResponse of(Long artistId) {
    return new ArtistAddResponse(artistId);
  }
}
