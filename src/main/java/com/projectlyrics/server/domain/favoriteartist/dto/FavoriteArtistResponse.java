package com.projectlyrics.server.domain.favoriteartist.dto;

import com.projectlyrics.server.domain.artist.dto.response.ArtistGetResponse;
import com.projectlyrics.server.domain.common.dto.util.CursorResponse;
import com.projectlyrics.server.domain.favoriteartist.entity.FavoriteArtist;

public record FavoriteArtistResponse(
        Long id,
        ArtistGetResponse artist
) implements CursorResponse {

    public static FavoriteArtistResponse of(FavoriteArtist favoriteArtist) {
        return new FavoriteArtistResponse(favoriteArtist.getId(), ArtistGetResponse.of(favoriteArtist.getArtist()));
    }

    @Override
    public long getId() {
        return id;
    }
}
