package com.projectlyrics.server.domain.artist.entity;

import com.projectlyrics.server.domain.artist.dto.request.ArtistUpdateRequest;

public record ArtistUpdate(
        String name,
        String secondName,
        String thirdName,
        String imageUrl
) {

    public static ArtistUpdate from(ArtistUpdateRequest request) {
        return new ArtistUpdate(
                request.name(),
                request.secondName(),
                request.thirdName(),
                request.imageUrl()
        );
    }
}
