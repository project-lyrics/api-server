package com.projectlyrics.server.domain.note.entity;

import com.projectlyrics.server.domain.note.dto.request.NoteUpdateRequest;

import static com.projectlyrics.server.domain.common.util.DomainUtils.checkNull;

public record NoteUpdate(
        String content,
        String lyrics,
        NoteBackground background,
        NoteStatus status
) {

    public static NoteUpdate from(NoteUpdateRequest request) {
        checkNull(request.status());

        return new NoteUpdate(
                request.content(),
                request.lyrics(),
                request.background(),
                request.status()
        );
    }
}
