package com.projectlyrics.server.domain.note.entity;

import com.projectlyrics.server.domain.note.dto.request.NoteCreateRequest;
import com.projectlyrics.server.domain.song.entity.Song;
import com.projectlyrics.server.domain.user.entity.User;

import static com.projectlyrics.server.domain.common.util.DomainUtils.checkNull;

public record NoteCreate(
        String content,
        String lyrics,
        NoteBackground background,
        NoteStatus status,
        User publisher,
        Song song
) {

    public static NoteCreate from(NoteCreateRequest request, User publisher, Song song) {
        checkNull(request.background());
        checkNull(request.status());
        checkNull(publisher);
        checkNull(song);

        return new NoteCreate(
                request.content(),
                request.lyrics(),
                request.background(),
                request.status(),
                publisher,
                song
        );
    }
}
