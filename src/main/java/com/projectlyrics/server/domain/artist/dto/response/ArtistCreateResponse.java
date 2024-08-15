package com.projectlyrics.server.domain.artist.dto.response;

public record ArtistCreateResponse(
        Long id
) {

    public static ArtistCreateResponse of(Long artistId) {
        return new ArtistCreateResponse(artistId);
    }
}
