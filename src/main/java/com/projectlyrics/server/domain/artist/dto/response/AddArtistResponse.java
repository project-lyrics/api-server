package com.projectlyrics.server.domain.artist.dto.response;

public record AddArtistResponse(
    Long id
) {

  public static AddArtistResponse of(Long artistId) {
    return new AddArtistResponse(artistId);
  }
}
