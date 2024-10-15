package com.projectlyrics.server.domain.notification.repository;

import com.projectlyrics.server.domain.notification.api.dto.response.NotificationGetResponse;
import com.projectlyrics.server.domain.notification.domain.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface NotificationQueryRepository {

    Notification findById(Long id);
    Slice<NotificationGetResponse> findAllByReceiverId(Long receiverId, Long cursorId, Pageable pageable);
    Slice<Notification> findAllBySenderId(Long senderId, Long cursorId, Pageable pageable);
    boolean hasUnchecked(Long receiverId);
}
