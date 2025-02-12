package com.projectlyrics.server.domain.event.repository.impl;

import static com.projectlyrics.server.domain.event.domain.QEventReceipt.eventReceipt;
import static com.projectlyrics.server.domain.user.entity.QUser.user;

import com.projectlyrics.server.domain.event.domain.EventReceipt;
import com.projectlyrics.server.domain.event.exception.EventReceiptNotFoundException;
import com.projectlyrics.server.domain.event.repository.EventReceiptQueryRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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
                                .and(user.id.eq(userId))
                                .and(eventReceipt.deletedAt.isNull())
                        )
                        .fetchFirst()
        ).orElseThrow(EventReceiptNotFoundException::new);
    }
}
