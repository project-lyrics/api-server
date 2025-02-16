package com.projectlyrics.server.domain.event.repository.impl;

import static com.projectlyrics.server.domain.event.domain.QEvent.event;
import static com.projectlyrics.server.domain.event.domain.QEventReceipt.eventReceipt;

import com.projectlyrics.server.domain.common.util.QueryDslUtils;
import com.projectlyrics.server.domain.event.domain.Event;
import com.projectlyrics.server.domain.event.exception.EventNotFoundException;
import com.projectlyrics.server.domain.event.repository.EventQueryRepository;
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
    public Slice<Event> findAllExceptRefusals(Long userId, Long cursorId, Pageable pageable) {
        List<Event> content = jpaQueryFactory
                .selectFrom(event)
                .leftJoin(eventReceipt).on(
                        eventReceipt.event.eq(event)
                                .and(eventReceipt.user.id.eq(userId))
                                .and(eventReceipt.deletedAt.isNull())
                                .and(eventReceipt.createdAt.goe(LocalDate.now().atStartOfDay()))
                )
                .fetchJoin()
                .where(
                        event.dueDate.after(LocalDateTime.now()),
                        eventReceipt.id.isNull().or(eventReceipt.refusal.isFalse()),
                        event.deletedAt.isNull(),
                        QueryDslUtils.ltCursorId(cursorId, event.id)
                )
                .orderBy(event.id.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return new SliceImpl<>(content, pageable, QueryDslUtils.checkIfHasNext(pageable, content));
    }
}
