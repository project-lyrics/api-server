package com.projectlyrics.server.domain.view.domain;

import com.projectlyrics.server.domain.note.entity.Note;
import com.projectlyrics.server.domain.user.entity.User;

public record TotalViewCreate(
        Note note,
        User user,
        String deviceId
) {
    public static TotalViewCreate of(Note note, User user, String deviceId) {
        return new TotalViewCreate(note, user, deviceId);
    }
}
