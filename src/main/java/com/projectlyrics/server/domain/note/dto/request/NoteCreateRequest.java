package com.projectlyrics.server.domain.note.dto.request;

import com.projectlyrics.server.domain.note.entity.NoteBackground;
import com.projectlyrics.server.domain.note.entity.NoteStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NoteCreateRequest(
        @NotBlank(message = "노트 내용이 입력되지 않았습니다.")
        String content,
        String lyrics,
        NoteBackground background,
        @NotNull
        NoteStatus status,
        Long songId
) {
}
