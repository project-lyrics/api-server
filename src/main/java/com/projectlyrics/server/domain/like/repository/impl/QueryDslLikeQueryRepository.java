package com.projectlyrics.server.domain.like.repository.impl;

import com.projectlyrics.server.domain.like.domain.Like;
import com.projectlyrics.server.domain.like.repository.LikeQueryRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.projectlyrics.server.domain.like.domain.QLike.like;

@Repository
@RequiredArgsConstructor
public class QueryDslLikeQueryRepository implements LikeQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Like> findByNoteIdAndUserId(Long noteId, Long userId) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(like)
                        .where(
                                like.note.id.eq(noteId),
                                like.user.id.eq(userId),
                                like.deletedAt.isNull()
                        )
                        .fetchOne()
        );
    }

    @Override
    public long countByNoteId(Long noteId) {
        return jpaQueryFactory
                .select(like.count())
                .from(like)
                .where(
                        like.note.id.eq(noteId),
                        like.deletedAt.isNull()
                )
                .fetchOne();
    }
}
