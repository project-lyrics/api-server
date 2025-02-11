package com.projectlyrics.server.domain.event.repository;

import com.projectlyrics.server.domain.event.domain.Event;

public interface EventQueryRepository {

    Event findById(Long id);
}
