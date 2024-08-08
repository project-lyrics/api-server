package com.projectlyrics.server.domain.note.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.projectlyrics.server.domain.comment.domain.Comment;
import com.projectlyrics.server.domain.comment.dto.response.CommentGetResponse;
import com.projectlyrics.server.domain.note.entity.Note;
import com.projectlyrics.server.domain.song.dto.response.SongGetResponse;
import com.projectlyrics.server.domain.user.dto.response.UserGetResponse;

import java.time.LocalDateTime;
import java.util.List;

public record NoteDetailResponse(
        Long id,
        String content,
        String status,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        LocalDateTime createdAt,
        LyricsGetResponse lyrics,
        UserGetResponse publisher,
        SongGetResponse song,
        int commentsCount,
        List<CommentGetResponse> comments
) {

    public static NoteDetailResponse of(Note note, List<Comment> comments) {
        return new NoteDetailResponse(
                note.getId(),
                note.getContent(),
                note.getNoteStatus().name(),
                note.getCreatedAt(),
                LyricsGetResponse.from(note.getLyrics()),
                UserGetResponse.from(note.getPublisher()),
                SongGetResponse.from(note.getSong()),
                comments.size(),
                comments.stream()
                        .map(CommentGetResponse::from)
                        .toList()
        );
    }

    public static NoteDetailResponse of(Note note, List<Comment> comments, LocalDateTime createdAt) {
        return new NoteDetailResponse(
                note.getId(),
                note.getContent(),
                note.getNoteStatus().name(),
                createdAt,
                LyricsGetResponse.from(note.getLyrics()),
                UserGetResponse.from(note.getPublisher()),
                SongGetResponse.from(note.getSong()),
                comments.size(),
                comments.stream()
                        .map(comment -> CommentGetResponse.from(comment, createdAt))
                        .toList()
        );
    }
}
