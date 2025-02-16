package com.projectlyrics.server.domain.event.repository;

import com.projectlyrics.server.domain.event.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventCommandRepository extends JpaRepository<Event, Long> {
}
