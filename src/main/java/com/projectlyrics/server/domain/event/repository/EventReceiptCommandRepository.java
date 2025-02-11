package com.projectlyrics.server.domain.event.repository;

import com.projectlyrics.server.domain.event.domain.EventReceipt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventReceiptCommandRepository extends JpaRepository<EventReceipt, Long> {
}
