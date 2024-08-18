package com.projectlyrics.server.domain.artist.dto.request;

public record ArtistUpdateRequest(
        String name,
        String secondName,
        String thirdName,
        String imageUrl
) {
}
