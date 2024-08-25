package com.projectlyrics.server.domain.notification.repository.impl;

import com.projectlyrics.server.domain.common.util.QueryDslUtils;
import com.projectlyrics.server.domain.notification.domain.Notification;
import com.projectlyrics.server.domain.notification.repository.NotificationQueryRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.projectlyrics.server.domain.notification.domain.QNotification.notification;

@Repository
@RequiredArgsConstructor
public class QueryDslNotificationQueryRepository implements NotificationQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Slice<Notification> findAllByReceiverId(Long receiverId, Long cursorId, Pageable pageable) {
        List<Notification> content = jpaQueryFactory
                .selectFrom(notification)
                .leftJoin(notification.sender).fetchJoin()
                .leftJoin(notification.receiver).fetchJoin()
                .leftJoin(notification.note).fetchJoin()
                .leftJoin(notification.comment).fetchJoin()
                .where(
                        notification.receiver.id.eq(receiverId),
                        QueryDslUtils.gtCursorId(cursorId, notification.id),
                        notification.deletedAt.isNull()
                )
                .orderBy(notification.id.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return new SliceImpl<>(content, pageable, QueryDslUtils.checkIfHasNext(pageable, content));
    }

    @Override
    public Slice<Notification> findAllBySenderId(Long senderId, Long cursorId, Pageable pageable) {
        List<Notification> content = jpaQueryFactory
                .selectFrom(notification)
                .leftJoin(notification.sender).fetchJoin()
                .leftJoin(notification.receiver).fetchJoin()
                .leftJoin(notification.note).fetchJoin()
                .leftJoin(notification.comment).fetchJoin()
                .where(
                        notification.sender.id.eq(senderId),
                        QueryDslUtils.gtCursorId(cursorId, notification.id),
                        notification.deletedAt.isNull()
                )
                .orderBy(notification.id.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return new SliceImpl<>(content, pageable, QueryDslUtils.checkIfHasNext(pageable, content));
    }
}
