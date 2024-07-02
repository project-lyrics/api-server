package com.projectlyrics.server.domain.favoriteartist.dto.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateFavoriteArtistListRequest(
        @NotEmpty
        List<Long> artistIds
) {
}
