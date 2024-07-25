package com.projectlyrics.server.domain.note.dto.request;

import com.projectlyrics.server.domain.note.entity.NoteBackground;
import com.projectlyrics.server.domain.note.entity.NoteStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NoteCreateRequest(
        @NotBlank(message = "노트 내용이 입력되지 않았습니다.")
        @Schema(name = "노트 내용")
        String content,
        @Schema(name = "가사")
        String lyrics,
        @Schema(name = "배경색")
        NoteBackground background,
        @NotNull
        @Schema(name = "노트 상태")
        NoteStatus status,
        @Schema(name = "작성자 ID")
        Long publisherId,
        @Schema(name = "곡 ID")
        Long songId
) {
}
