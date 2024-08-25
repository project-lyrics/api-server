package com.projectlyrics.server.domain.notification.domain.event;

import com.projectlyrics.server.domain.user.entity.User;

public record PublicEvent(
        User sender,
        User receiver,
        String content
) {

    public static PublicEvent of(String content, User sender, User receiver) {
        return new PublicEvent(sender, receiver, content);
    }
}
