package com.projectlyrics.server.domain.note.dto.request;

import com.projectlyrics.server.domain.note.entity.NoteBackground;
import com.projectlyrics.server.domain.note.entity.NoteStatus;

public record NoteUpdateRequest(
        String content,
        String lyrics,
        NoteBackground background,
        NoteStatus status
) {
}
