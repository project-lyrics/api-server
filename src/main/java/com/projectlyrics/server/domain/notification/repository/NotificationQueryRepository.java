package com.projectlyrics.server.domain.notification.repository;

import com.projectlyrics.server.domain.notification.api.dto.response.NotificationGetResponse;
import com.projectlyrics.server.domain.notification.domain.Notification;
import com.projectlyrics.server.domain.notification.domain.NotificationType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface NotificationQueryRepository {

    Notification findById(Long id);
    Slice<NotificationGetResponse> findAllByTypeAndReceiverId(NotificationType type, Long receiverId, Long cursorId, int size);
    Slice<Notification> findAllBySenderId(Long senderId, Long cursorId, Pageable pageable);
    boolean hasUnchecked(Long receiverId);
}
