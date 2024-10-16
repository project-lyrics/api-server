package com.projectlyrics.server.domain.notification.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.projectlyrics.server.domain.notification.domain.Notification;
import com.projectlyrics.server.domain.notification.domain.event.CommentEvent;
import com.projectlyrics.server.domain.notification.domain.event.DisciplineEvent;
import com.projectlyrics.server.domain.notification.domain.event.PublicEvent;
import com.projectlyrics.server.domain.notification.repository.NotificationCommandRepository;
import com.projectlyrics.server.domain.notification.repository.NotificationQueryRepository;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.exception.UserNotFoundException;
import com.projectlyrics.server.domain.user.repository.UserQueryRepository;
import java.util.List;
import java.util.concurrent.CompletableFuture;
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
    private final NotificationQueryRepository notificationQueryRepository;
    private final UserQueryRepository userQueryRepository;

    @Async
    @EventListener
    public CompletableFuture<Void> createCommentNotification(CommentEvent event) {
        notificationCommandRepository.save(Notification.create(event));
        return CompletableFuture.completedFuture(null);
    }

    @Async
    @EventListener
    public CompletableFuture<Void> createDisciplineNotification(DisciplineEvent event) {
        notificationCommandRepository.save(Notification.create(event));
        return CompletableFuture.completedFuture(null);
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
    }

    public void check(Long id, Long userId) {
        Notification notification = notificationQueryRepository.findById(id);
        notification.check(userId);
    }
}
