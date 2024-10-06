package com.projectlyrics.server.domain.notification.domain.event;

import com.projectlyrics.server.domain.discipline.domain.Discipline;
import com.projectlyrics.server.domain.user.entity.User;

public record DisciplineEvent (
        User sender,
        User receiver,
        Discipline discipline
) {

    public static DisciplineEvent from(User sender, Discipline discipline) {
        return new DisciplineEvent(
                sender,
                discipline.getUser(),
                discipline
        );
    }
}
