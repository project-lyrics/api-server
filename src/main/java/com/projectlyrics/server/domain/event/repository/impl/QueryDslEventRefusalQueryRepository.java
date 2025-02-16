package com.projectlyrics.server.domain.event.repository.impl;

import static com.projectlyrics.server.domain.event.domain.QEventRefusal.eventRefusal;
import static com.projectlyrics.server.domain.user.entity.QUser.user;

import com.projectlyrics.server.domain.event.domain.EventRefusal;
import com.projectlyrics.server.domain.event.exception.EventRefusalNotFoundException;
import com.projectlyrics.server.domain.event.repository.EventRefusalQueryRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QueryDslEventRefusalQueryRepository implements EventRefusalQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public EventRefusal findByEventIdAndUserId(Long eventId, Long userId) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(eventRefusal)
                        .where(eventRefusal.event.id.eq(eventId)
                                .and(user.id.eq(userId))
                                .and(eventRefusal.deletedAt.isNull())
                        )
                        .fetchFirst()
        ).orElseThrow(EventRefusalNotFoundException::new);
    }
}
