package com.projectlyrics.server.domain.artist.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record ArtistAddRequest(
        @NotBlank
        @Schema(name = "아티스트의 이름")
        String name,

        @Schema(name = "아티스트의 cdn 이미지 경로")
        String imageUrl
) {
}
