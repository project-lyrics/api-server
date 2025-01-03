package com.projectlyrics.server.domain.notification.repository;

import com.projectlyrics.server.domain.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationCommandRepository extends JpaRepository<Notification, Long> {

    void deleteAllByReceiverId(Long receiverId);
    void deleteAllBySenderId(Long senderId);
}
