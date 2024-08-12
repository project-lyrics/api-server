package com.projectlyrics.server.domain.note.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.projectlyrics.server.domain.common.dto.util.CursorResponse;
import com.projectlyrics.server.domain.like.domain.Like;
import com.projectlyrics.server.domain.note.entity.Note;
import com.projectlyrics.server.domain.song.dto.response.SongGetResponse;
import com.projectlyrics.server.domain.user.dto.response.UserGetResponse;

import java.time.LocalDateTime;
import java.util.List;

public record NoteGetResponse(
        Long id,
        String content,
        String status,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        LocalDateTime createdAt,
        LyricsGetResponse lyrics,
        UserGetResponse publisher,
        SongGetResponse song,
        int commentsCount,
        int likesCount,
        boolean isLiked
) implements CursorResponse {

    public static NoteGetResponse of(Note note, Long userId) {
        return new NoteGetResponse(
                note.getId(),
                note.getContent(),
                note.getNoteStatus().name(),
                note.getCreatedAt(),
                LyricsGetResponse.from(note.getLyrics()),
                UserGetResponse.from(note.getPublisher()),
                SongGetResponse.from(note.getSong()),
                note.getComments().size(),
                note.getLikes().size(),
                isLiked(note.getLikes(), userId)
        );
    }

    public static NoteGetResponse of(Note note, Long userId, LocalDateTime createdAt) {
        return new NoteGetResponse(
                note.getId(),
                note.getContent(),
                note.getNoteStatus().name(),
                createdAt,
                LyricsGetResponse.from(note.getLyrics()),
                UserGetResponse.from(note.getPublisher()),
                SongGetResponse.from(note.getSong()),
                note.getComments().size(),
                note.getLikes().size(),
                isLiked(note.getLikes(), userId)
        );
    }

    private static boolean isLiked(List<Like> likes, Long userId) {
        return likes.stream()
                .anyMatch(like -> like.isUser(userId));
    }

    @Override
    public long getId() {
        return id;
    }
}
