package com.projectlyrics.server.domain.artist.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record ArtistUpdateRequest(
        @Schema(name = "아티스트의 한글 이름")
        String name,
        String profileImageCdnLink
) {
}
