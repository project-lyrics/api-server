package com.projectlyrics.server.domain.event.repository.impl;

import com.projectlyrics.server.domain.event.domain.Event;
import com.projectlyrics.server.domain.event.exception.EventNotFoundException;
import com.projectlyrics.server.domain.event.repository.EventQueryRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.projectlyrics.server.domain.event.domain.QEvent.event;

@Repository
@RequiredArgsConstructor
public class QueryDslEventQueryRepository implements EventQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Event findById(Long id) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(event)
                        .where(event.id.eq(id))
                        .fetchFirst()
        ).orElseThrow(EventNotFoundException::new);
    }
}
