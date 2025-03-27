package com.projectlyrics.server.domain.view.domain;

import com.projectlyrics.server.domain.note.entity.Note;
import com.projectlyrics.server.domain.user.entity.User;

public record ViewCreate(
        Note note,
        User user,
        String deviceId
) {
    public static ViewCreate of(Note note, User user, String deviceId) {
        return new ViewCreate(note, user, deviceId);
    }
}
