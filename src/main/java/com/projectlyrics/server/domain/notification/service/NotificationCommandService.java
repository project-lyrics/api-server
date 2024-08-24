package com.projectlyrics.server.domain.notification.service;

import com.projectlyrics.server.domain.notification.domain.Notification;
import com.projectlyrics.server.domain.notification.domain.event.CommentEvent;
import com.projectlyrics.server.domain.notification.repository.NotificationCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationCommandService {

    private final NotificationCommandRepository notificationCommandRepository;

    @Async
    @EventListener
    public void createCommentNotification(CommentEvent event) {
        Notification notification = notificationCommandRepository.save(Notification.create(event));

    }
}
