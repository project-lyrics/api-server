package com.projectlyrics.server.domain.event.repository;

import com.projectlyrics.server.domain.event.domain.EventReceipt;

public interface EventReceiptQueryRepository {

    EventReceipt findByEventIdAndUserId(Long eventId, Long userId);
}
