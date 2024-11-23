package com.projectlyrics.server.support.fixture;

import com.projectlyrics.server.domain.notification.domain.Notification;
import com.projectlyrics.server.domain.notification.domain.event.PublicEvent;
import com.projectlyrics.server.domain.user.entity.User;

public class NotificationFixture extends BaseFixture{

    public static Notification create(User sender, User receiver){
        return Notification.create(
                PublicEvent.of("공통알림내용", sender, receiver)
        );
    }
}
