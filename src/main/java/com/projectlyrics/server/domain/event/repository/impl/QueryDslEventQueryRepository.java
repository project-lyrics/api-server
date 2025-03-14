package com.projectlyrics.server.domain.event.repository.impl;

import static com.projectlyrics.server.domain.event.domain.QEvent.event;
import static com.projectlyrics.server.domain.event.domain.QEventRefusal.eventRefusal;

import com.projectlyrics.server.domain.common.util.QueryDslUtils;
import com.projectlyrics.server.domain.event.domain.Event;
import com.projectlyrics.server.domain.event.exception.EventNotFoundException;
import com.projectlyrics.server.domain.event.repository.EventQueryRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QueryDslEventQueryRepository implements EventQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Event findById(Long id) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(event)
                        .where(event.id.eq(id),
                                event.deletedAt.isNull())
                        .fetchFirst()
        ).orElseThrow(EventNotFoundException::new);
    }

    @Override
    public Slice<Event> findAllExceptRefusedByUserId(Long userId, Long cursorId, Pageable pageable) {
        return findAllExceptRefusals(eventRefusal.user.id.eq(userId), cursorId, pageable);
    }

    @Override
    public Slice<Event> findAllExceptRefusedByDeviceId(String deviceId, Long cursorId, Pageable pageable) {
        return findAllExceptRefusals(eventRefusal.deviceId.eq(deviceId), cursorId, pageable);
    }

    private Slice<Event> findAllExceptRefusals(
            BooleanExpression filterCondition,
            Long cursorId,
            Pageable pageable
    ) {
        List<Event> content = jpaQueryFactory
                .selectFrom(event)
                .leftJoin(eventRefusal).on(
                        eventRefusal.event.eq(event)
                                .and(filterCondition)
                                .and(eventRefusal.deletedAt.isNull())
                                .and(eventRefusal.deadline.after(LocalDate.now()))
                )
                .fetchJoin()
                .where(
                        event.dueDate.after(LocalDateTime.now()),
                        eventRefusal.id.isNull().or(eventRefusal.refusal.isFalse()),
                        event.deletedAt.isNull(),
                        QueryDslUtils.ltCursorId(cursorId, event.id)
                )
                .orderBy(event.id.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return new SliceImpl<>(content, pageable, QueryDslUtils.checkIfHasNext(pageable, content));
    }
}
