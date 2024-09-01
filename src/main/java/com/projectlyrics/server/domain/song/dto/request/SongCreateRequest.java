package com.projectlyrics.server.domain.song.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record SongCreateRequest(
        @NotNull(message = "곡 ID가 입력되지 않았습니다.")
        Long id,
        @NotBlank(message = "스포티파이 ID가 입력되지 않았습니다.")
        String spotifyId,
        @NotBlank(message = "곡 제목이 입력되지 않았습니다.")
        String name,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        LocalDate releaseDate,
        @NotBlank(message = "앨범 이름이 입력되지 않았습니다.")
        String albumName,
        String imageUrl,
        @NotNull
        Long artistId
) {
}
