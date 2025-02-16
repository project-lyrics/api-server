package com.projectlyrics.server.domain.event.repository;

import com.projectlyrics.server.domain.event.domain.EventRefusal;
import java.util.Optional;

public interface EventRefusalQueryRepository {

    Optional<EventRefusal> findByEventIdAndUserId(Long eventId, Long userId);
}
