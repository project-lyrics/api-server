package com.projectlyrics.server.domain.note.dto.response;

import com.projectlyrics.server.domain.common.dto.util.CursorResponse;
import com.projectlyrics.server.domain.note.entity.Note;

import java.time.LocalDateTime;

public record NoteGetResponse(
        Long id,
        String content,
        String lyrics,
        String background,
        String status,
        LocalDateTime createdAt,
        Long publisherId,
        String publisherNickname,
        String publisherProfileCharacterType,
        Long songId,
        String songName,
        String songImageUrl
) implements CursorResponse {

    public static NoteGetResponse from(Note note) {
        return new NoteGetResponse(
                note.getId(),
                note.getContent(),
                note.getLyrics().getContent(),
                note.getLyrics().getBackground().name(),
                note.getNoteStatus().name(),
                note.getCreatedAt(),
                note.getPublisher().getId(),
                note.getPublisher().getNickname().getValue(),
                note.getPublisher().getProfileCharacter().getType(),
                note.getSong().getId(),
                note.getSong().getName(),
                note.getSong().getImageUrl()
        );
    }

    @Override
    public long getId() {
        return id;
    }
}
