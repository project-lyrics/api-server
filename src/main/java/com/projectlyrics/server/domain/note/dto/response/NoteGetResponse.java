package com.projectlyrics.server.domain.note.dto.response;

import com.projectlyrics.server.domain.common.dto.util.CursorResponse;
import com.projectlyrics.server.domain.note.entity.Note;
import com.projectlyrics.server.domain.song.dto.response.SongGetResponse;
import com.projectlyrics.server.domain.user.dto.response.UserGetResponse;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record NoteGetResponse(
        Long id,
        String content,
        String status,
        String createdAt,
        LyricsGetResponse lyrics,
        UserGetResponse publisher,
        SongGetResponse song
) implements CursorResponse {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");

    public static NoteGetResponse from(Note note) {
        return new NoteGetResponse(
                note.getId(),
                note.getContent(),
                note.getNoteStatus().name(),
                formatTime(note.getCreatedAt()),
                LyricsGetResponse.from(note.getLyrics()),
                UserGetResponse.from(note.getPublisher()),
                SongGetResponse.from(note.getSong())
        );
    }

    public static NoteGetResponse of(Note note, LocalDateTime createdAt) {
        return new NoteGetResponse(
                note.getId(),
                note.getContent(),
                note.getNoteStatus().name(),
                formatTime(createdAt),
                LyricsGetResponse.from(note.getLyrics()),
                UserGetResponse.from(note.getPublisher()),
                SongGetResponse.from(note.getSong())
        );
    }

    private static String formatTime(LocalDateTime time) {
        return time.format(DATE_TIME_FORMATTER);
    }

    @Override
    public long getId() {
        return id;
    }
}
