package com.projectlyrics.server.domain.notification.service;

import com.projectlyrics.server.domain.common.dto.util.CursorBasePaginatedResponse;
import com.projectlyrics.server.domain.notification.api.dto.response.NotificationGetResponse;
import com.projectlyrics.server.domain.notification.repository.NotificationQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class NotificationQueryService {

    private final NotificationQueryRepository notificationQueryRepository;

    public CursorBasePaginatedResponse<NotificationGetResponse> getRecentNotifications(Long userId, Long cursor, int size) {
        return CursorBasePaginatedResponse.of(
                notificationQueryRepository.findAllByReceiverId(userId, cursor, Pageable.ofSize(size))
        );
    }

    public boolean hasUnchecked(Long userId) {
        return notificationQueryRepository.hasUnchecked(userId);
    }
}
