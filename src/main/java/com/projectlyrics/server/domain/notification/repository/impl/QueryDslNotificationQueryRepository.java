package com.projectlyrics.server.domain.notification.repository.impl;

import static com.projectlyrics.server.domain.artist.entity.QArtist.artist;
import static com.projectlyrics.server.domain.note.entity.QNote.note;
import static com.projectlyrics.server.domain.notification.domain.QNotification.notification;
import static com.projectlyrics.server.domain.song.entity.QSong.song;

import com.projectlyrics.server.domain.common.util.QueryDslUtils;
import com.projectlyrics.server.domain.notification.api.dto.response.NotificationGetResponse;
import com.projectlyrics.server.domain.notification.domain.Notification;
import com.projectlyrics.server.domain.notification.domain.NotificationType;
import com.projectlyrics.server.domain.notification.exception.NotificationNotFoundException;
import com.projectlyrics.server.domain.notification.repository.NotificationQueryRepository;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QueryDslNotificationQueryRepository implements NotificationQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    private static final ConstructorExpression<NotificationGetResponse> notificationGetResponse = Projections.constructor(
            NotificationGetResponse.class,
            notification.id,
            notification.type,
            notification.content,
            notification.createdAt,
            notification.checked,
            note.id,
            note.content,
            artist.imageUrl
    );

    @Override
    public Notification findById(Long id) {
        Notification result = jpaQueryFactory
                .selectFrom(notification)
                .join(notification.receiver).fetchJoin()
                .where(notification.id.eq(id))
                .fetchOne();

        if (Objects.nonNull(result))
            return result;

        throw new NotificationNotFoundException();
    }

    @Override
    public Slice<NotificationGetResponse> findAllByTypesAndReceiverId(List<NotificationType> types, Long receiverId, Long cursorId, int size) {
        List<NotificationGetResponse> content = jpaQueryFactory
                .select(notificationGetResponse)
                .from(notification)
                .leftJoin(notification.note, note)
                .leftJoin(note.song, song)
                .leftJoin(song.artist, artist)
                .where(
                        notification.receiver.id.eq(receiverId),
                        QueryDslUtils.ltCursorId(cursorId, notification.id),
                        notification.deletedAt.isNull(),
                        notification.type.in(types)
                )
                .orderBy(notification.id.desc())
                .limit(size + 1)
                .fetch();

        return new SliceImpl<>(content, PageRequest.ofSize(size), QueryDslUtils.checkIfHasNext(size, content));
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
                        QueryDslUtils.ltCursorId(cursorId, notification.id),
                        notification.deletedAt.isNull()
                )
                .orderBy(notification.id.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return new SliceImpl<>(content, pageable, QueryDslUtils.checkIfHasNext(pageable, content));
    }

    @Override
    public boolean hasUnchecked(Long receiverId) {
        Integer result = jpaQueryFactory
                .selectOne()
                .from(notification)
                .where(
                        notification.receiver.id.eq(receiverId)
                                .and(notification.checked.isFalse())
                )
                .fetchFirst();

        return result != null;
    }
}
