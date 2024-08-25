package com.projectlyrics.server.domain.notification.repository;

import com.projectlyrics.server.domain.notification.domain.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface NotificationQueryRepository {

    Slice<Notification> findAllByReceiverId(Long receiverId, Long cursorId, Pageable pageable);
}
