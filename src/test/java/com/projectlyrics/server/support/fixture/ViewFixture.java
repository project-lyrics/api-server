package com.projectlyrics.server.support.fixture;

import com.projectlyrics.server.domain.note.entity.Note;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.view.domain.View;
import com.projectlyrics.server.domain.view.domain.ViewCreate;

public class ViewFixture extends BaseFixture{
    public static View create(Note note, User user, String deviceId) {
        return View.createWithId(
                getUniqueId(),
                ViewCreate.of(
                        note,
                        user,
                        deviceId
                )
        );
    }
}
