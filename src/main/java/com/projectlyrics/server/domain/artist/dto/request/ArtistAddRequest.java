package com.projectlyrics.server.domain.artist.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ArtistAddRequest(
        @NotBlank(message = "아티스트의 이름이 입력되지 않았습니다.")
        String name,
        String imageUrl
) {
}
