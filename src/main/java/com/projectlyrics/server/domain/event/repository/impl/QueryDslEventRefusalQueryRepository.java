package com.projectlyrics.server.domain.event.repository.impl;

import static com.projectlyrics.server.domain.event.domain.QEventRefusal.eventRefusal;

import com.projectlyrics.server.domain.event.domain.EventRefusal;
import com.projectlyrics.server.domain.event.repository.EventRefusalQueryRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QueryDslEventRefusalQueryRepository implements EventRefusalQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public Optional<EventRefusal> findByEventIdAndUserId(Long eventId, Long userId) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(eventRefusal)
                        .where(eventRefusal.event.id.eq(eventId)
                                .and(eventRefusal.user.id.eq(userId))
                                .and(eventRefusal.deletedAt.isNull())
                        )
                        .fetchFirst()
        );
    }
}
