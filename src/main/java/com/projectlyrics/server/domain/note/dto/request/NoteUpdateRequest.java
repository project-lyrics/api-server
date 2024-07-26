package com.projectlyrics.server.domain.note.dto.request;

import com.projectlyrics.server.domain.note.entity.NoteBackground;
import com.projectlyrics.server.domain.note.entity.NoteStatus;
import jakarta.validation.constraints.NotNull;

public record NoteUpdateRequest(
        @NotNull(message = "노트 ID가 입력되지 않았습니다.")
        Long noteId,
        String content,
        String lyrics,
        NoteBackground background,
        NoteStatus status,
        Long publisherId
) {
}
