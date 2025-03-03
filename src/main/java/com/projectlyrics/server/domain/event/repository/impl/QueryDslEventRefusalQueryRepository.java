package com.projectlyrics.server.domain.event.repository.impl;

import com.projectlyrics.server.domain.event.domain.EventRefusal;
import com.projectlyrics.server.domain.event.domain.QEventRefusal;
import com.projectlyrics.server.domain.event.exception.EventRefusalNotFoundException;
import com.projectlyrics.server.domain.event.repository.EventRefusalQueryRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QueryDslEventRefusalQueryRepository implements EventRefusalQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public EventRefusal findByEventIdAndUserId(Long eventId, Long userId) {
        EventRefusal eventRefusal = jpaQueryFactory
                .selectFrom(QEventRefusal.eventRefusal)
                .where(QEventRefusal.eventRefusal.event.id.eq(eventId)
                        .and(QEventRefusal.eventRefusal.user.id.eq(userId))
                        .and(QEventRefusal.eventRefusal.deletedAt.isNull())
                )
                .fetchFirst();

        if (eventRefusal == null) {
            throw new EventRefusalNotFoundException();
        }

        return eventRefusal;
    }

    @Override
    public EventRefusal findByEventIdAndDeviceId(Long eventId, String deviceId) {
        EventRefusal eventRefusal = jpaQueryFactory
                .selectFrom(QEventRefusal.eventRefusal)
                .where(QEventRefusal.eventRefusal.event.id.eq(eventId)
                        .and(QEventRefusal.eventRefusal.deviceId.eq(deviceId))
                        .and(QEventRefusal.eventRefusal.deletedAt.isNull())
                )
                .fetchFirst();

        if (eventRefusal == null) {
            throw new EventRefusalNotFoundException();
        }

        return eventRefusal;
    }
}
