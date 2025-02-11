package com.projectlyrics.server.domain.event.domain;

import com.projectlyrics.server.domain.user.entity.User;

public record EventReceiptCreate(
        Event event,
        User user
) {
}
