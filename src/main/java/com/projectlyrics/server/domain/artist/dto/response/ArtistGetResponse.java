package com.projectlyrics.server.domain.artist.dto.response;

import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.common.dto.util.CursorResponse;

public record ArtistGetResponse(
        Long id,
        String name,
        String imageUrl
) implements CursorResponse {

    public static ArtistGetResponse of(Artist artist) {
        return new ArtistGetResponse(
                artist.getId(),
                artist.getName(),
                artist.getImageUrl()
        );
    }

    @Override
    public long getId() {
        return id;
    }
}
