package com.projectlyrics.server.domain.event.repository;

import com.projectlyrics.server.domain.event.domain.EventRefusal;

public interface EventRefusalQueryRepository {

    EventRefusal findByEventIdAndUserId(Long eventId, Long userId);
    EventRefusal findByEventIdAndDeviceId(Long eventId, String deviceId);
}
