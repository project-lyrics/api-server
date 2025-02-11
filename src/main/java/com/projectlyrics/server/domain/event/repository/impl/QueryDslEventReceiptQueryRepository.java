package com.projectlyrics.server.domain.event.repository.impl;

import com.projectlyrics.server.domain.event.domain.EventReceipt;
import com.projectlyrics.server.domain.event.exception.EventReceiptNotFoundException;
import com.projectlyrics.server.domain.event.repository.EventReceiptQueryRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.projectlyrics.server.domain.event.domain.QEventReceipt.eventReceipt;
import static com.projectlyrics.server.domain.user.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class QueryDslEventReceiptQueryRepository implements EventReceiptQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public EventReceipt findByEventIdAndUserId(Long eventId, Long userId) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(eventReceipt)
                        .where(eventReceipt.event.id.eq(eventId)
                                .and(user.id.eq(userId)))
                        .fetchFirst()
        ).orElseThrow(EventReceiptNotFoundException::new);
    }
}
