package com.projectlyrics.server.domain.artist.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record ArtistUpdateRequest(
        @Schema(name = "아티스트의 한글 이름")
        String name,
        @Schema(name = "아티스트의 영어 이름")
        String englishName,
        @Schema(name = "아티스트의 cdn 이미지 경로")
        String profileImageCdnLink
) {
}
