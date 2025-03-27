package com.projectlyrics.server.domain.view.domain;

import com.projectlyrics.server.domain.note.entity.Note;

public record UniqueViewCreate(
        Note note,
        String deviceId
) {
    public static UniqueViewCreate of(Note note, String deviceId) {
        return new UniqueViewCreate(note, deviceId);
    }
}
