package com.projectlyrics.server.domain.notification.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;
import com.projectlyrics.server.domain.notification.domain.Notification;
import com.projectlyrics.server.domain.notification.domain.event.CommentEvent;
import com.projectlyrics.server.domain.notification.domain.event.PublicEvent;
import com.projectlyrics.server.domain.notification.exception.FailedToSendNotificationException;
import com.projectlyrics.server.domain.notification.repository.NotificationCommandRepository;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.exception.UserNotFoundException;
import com.projectlyrics.server.domain.user.repository.UserQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class NotificationCommandService {

    private final NotificationCommandRepository notificationCommandRepository;
    private final UserQueryRepository userQueryRepository;
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

    public void createPublicNotification(Long adminId, String content) {
        // TODO: adminId 확인하는 로직 추가
        User admin = userQueryRepository.findById(adminId)
                .orElseThrow(UserNotFoundException::new);
        List<User> receivers = userQueryRepository.findAll();

        List<Notification> notifications = receivers.stream()
                .map(receiver -> Notification.create(PublicEvent.of(content, admin, receiver)))
                .toList();
        notificationCommandRepository.saveAll(notifications);

        sendAll(notifications, content);
    }

    private void sendAll(List<Notification> notifications, String content) {
        int batchSize = notifications.size() / 500 + 1;

        for (int i = 0; i < batchSize; i++) {
            List<String> fcmTokens = notifications.subList(i * 500, Math.min((i + 1) * 500, notifications.size()))
                    .stream()
                    .map(Notification::getReceiver)
                    .map(User::getFcmToken)
                    .toList();

            MulticastMessage message = MulticastMessage.builder()
                    .addAllTokens(fcmTokens)
                    .putData("content", content)
                    .build();

            try {
                firebaseMessaging.sendEachForMulticast(message);
            } catch (FirebaseMessagingException e) {
                log.info(e.getMessage());
                throw new FailedToSendNotificationException();
            }
        }
    }
}
