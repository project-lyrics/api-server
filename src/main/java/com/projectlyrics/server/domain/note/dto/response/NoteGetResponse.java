package com.projectlyrics.server.domain.note.dto.response;

import com.projectlyrics.server.domain.common.dto.util.CursorResponse;
import com.projectlyrics.server.domain.common.util.DomainUtils;
import com.projectlyrics.server.domain.note.entity.Note;
import com.projectlyrics.server.domain.song.dto.response.SongGetResponse;
import com.projectlyrics.server.domain.user.dto.response.UserGetResponse;

public record NoteGetResponse(
        Long id,
        String content,
        String status,
        String createdAt,
        LyricsGetResponse lyrics,
        UserGetResponse publisher,
        SongGetResponse song
) implements CursorResponse {

    public static NoteGetResponse from(Note note) {
        return new NoteGetResponse(
                note.getId(),
                note.getContent(),
                note.getNoteStatus().name(),
                DomainUtils.formatTime(note.getCreatedAt()),
                LyricsGetResponse.from(note.getLyrics()),
                UserGetResponse.from(note.getPublisher()),
                SongGetResponse.from(note.getSong())
        );
    }

    @Override
    public long getId() {
        return id;
    }
}