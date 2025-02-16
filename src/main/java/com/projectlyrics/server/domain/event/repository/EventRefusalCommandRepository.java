package com.projectlyrics.server.domain.event.repository;

import com.projectlyrics.server.domain.event.domain.EventRefusal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRefusalCommandRepository extends JpaRepository<EventRefusal, Long> {
}
