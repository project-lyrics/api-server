package com.projectlyrics.server.domain.artist.entity;

import com.projectlyrics.server.domain.artist.dto.request.ArtistCreateRequest;

public record ArtistCreate(
        Long id,
        String name,
        String secondName,
        String thirdName,
        String spotifyId,
        String imageUrl
) {

    public static ArtistCreate from(ArtistCreateRequest request) {
        return new ArtistCreate(
                null,
                request.name(),
                request.secondName(),
                request.thirdName(),
                request.spotifyId(),
                request.imageUrl()
        );
    }
}
