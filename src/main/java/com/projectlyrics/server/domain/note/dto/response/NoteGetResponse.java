package com.projectlyrics.server.domain.note.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.projectlyrics.server.domain.common.dto.util.CursorResponse;
import com.projectlyrics.server.domain.note.entity.Note;
import com.projectlyrics.server.domain.song.dto.response.SongGetResponse;
import com.projectlyrics.server.domain.user.dto.response.UserGetResponse;

import java.time.LocalDateTime;

public record NoteGetResponse(
        Long id,
        String content,
        String status,
        String noteType,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        LocalDateTime createdAt,
        LyricsGetResponse lyrics,
        UserGetResponse publisher,
        SongGetResponse song,
        int commentsCount,
        int likesCount,
        boolean isLiked,
        boolean isBookmarked
) implements CursorResponse {

    public static NoteGetResponse of(Note note, Long userId) {
        return new NoteGetResponse(
                note.getId(),
                note.getContent(),
                note.getNoteStatus().name(),
                note.getNoteType().name(),
                note.getCreatedAt(),
                LyricsGetResponse.from(note.getLyrics()),
                UserGetResponse.from(note.getPublisher()),
                SongGetResponse.from(note.getSong()),
                note.getComments().size(),
                note.getLikes().size(),
                note.isLiked(userId),
                note.isBookmarked(userId)
        );
    }

    public static NoteGetResponse of(Note note, Long userId, LocalDateTime createdAt) {
        return new NoteGetResponse(
                note.getId(),
                note.getContent(),
                note.getNoteStatus().name(),
                note.getNoteType().name(),
                createdAt,
                LyricsGetResponse.from(note.getLyrics()),
                UserGetResponse.from(note.getPublisher()),
                SongGetResponse.from(note.getSong()),
                note.getComments().size(),
                note.getLikes().size(),
                note.isLiked(userId),
                note.isBookmarked(userId)
        );
    }

    @Override
    public long getId() {
        return id;
    }
}
