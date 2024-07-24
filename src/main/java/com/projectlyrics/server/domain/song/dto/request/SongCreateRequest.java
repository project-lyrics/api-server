package com.projectlyrics.server.domain.song.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record SongCreateRequest(
        @NotBlank(message = "스포티파이 ID가 입력되지 않았습니다.")
        @Schema(name = "스포티파이 ID")
        String spotifyId,
        @NotBlank(message = "곡 제목이 입력되지 않았습니다.")
        @Schema(name = "곡 제목")
        String name,
        @NotBlank(message = "곡 발매일이 입력되지 않았습니다.")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        @Schema(name = "곡 발매일")
        LocalDate releaseDate,
        @NotBlank(message = "앨범 이름이 입력되지 않았습니다.")
        @Schema(name = "앨범 이름")
        String albumName,
        @Schema(name = "곡 이미지 URL")
        String imageUrl,
        @NotNull
        @Schema(name = "아티스트 ID")
        Long artistId
) {
}
