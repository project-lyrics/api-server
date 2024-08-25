package com.projectlyrics.server.domain.notification.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.projectlyrics.server.domain.notification.domain.Notification;
import com.projectlyrics.server.domain.notification.domain.event.CommentEvent;
import com.projectlyrics.server.domain.notification.exception.FailedToSendNotificationException;
import com.projectlyrics.server.domain.notification.repository.NotificationCommandRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class NotificationCommandService {

    private final NotificationCommandRepository notificationCommandRepository;
    private final FirebaseMessaging firebaseMessaging;

    @Async
    @EventListener
    public void createCommentNotification(CommentEvent event) {
        Notification notification = notificationCommandRepository.save(Notification.create(event));
        send(notification);
    }

    private void send(Notification notification) {
        try {
            firebaseMessaging.send(notification.getMessage());
        } catch (FirebaseMessagingException e) {
            log.info(e.getMessage());
            throw new FailedToSendNotificationException();
        }
    }
}
