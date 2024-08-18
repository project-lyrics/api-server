package com.projectlyrics.server.domain.artist.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ArtistCreateRequest(
        @NotBlank(message = "아티스트의 이름이 입력되지 않았습니다.")
        String name,
        String secondName,
        String thirdName,
        String spotifyId,
        String imageUrl
) {
}
