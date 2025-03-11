package com.projectlyrics.server.domain.event.domain;

import com.projectlyrics.server.domain.user.entity.User;

public record EventRefusalCreateByUser(
        Event event,
        User user,
        int refusalPeriod
) {
}
