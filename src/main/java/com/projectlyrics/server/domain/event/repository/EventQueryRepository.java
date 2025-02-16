package com.projectlyrics.server.domain.event.repository;

import com.projectlyrics.server.domain.event.domain.Event;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface EventQueryRepository {

    Event findById(Long id);

    Slice<Event> findAllExceptRefusals(Long userId, Long cursorId, Pageable pageable);
}
