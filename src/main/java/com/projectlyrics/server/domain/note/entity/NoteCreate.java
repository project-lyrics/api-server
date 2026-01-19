package com.projectlyrics.server.domain.note.entity;

import com.projectlyrics.server.domain.note.dto.request.NoteCreateRequest;
import com.projectlyrics.server.domain.note.exception.NoLyricsForNoteException;
import com.projectlyrics.server.domain.song.entity.Song;
import com.projectlyrics.server.domain.user.entity.User;

import static com.projectlyrics.server.domain.common.util.DomainUtils.checkNull;

public record NoteCreate(
        String content,
        String lyrics,
        NoteBackground background,
        NoteStatus status,
        NoteType noteType,
        User publisher,
        Song song
) {

    public static NoteCreate from(NoteCreateRequest request, User publisher, Song song) {
        checkNull(request.status());
        checkNull(request.noteType());
        checkNull(publisher);
        checkNull(song);
        validateLyricsForType(request.noteType(), request.lyrics());

        return new NoteCreate(
                request.content(),
                request.lyrics(),
                request.background(),
                request.status(),
                request.noteType(),
                publisher,
                song
        );
    }

    private static void validateLyricsForType(NoteType noteType, String lyrics) {
        if (noteType == NoteType.LYRICS_ANALYSIS && (lyrics == null || lyrics.isBlank())) {
            throw new NoLyricsForNoteException();
        }
    }
}
