package com.projectlyrics.server.domain.event.domain;

import com.projectlyrics.server.domain.user.entity.User;

public record EventRefusalCreate(
        Event event,
        User user
) {
}
