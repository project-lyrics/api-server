package com.projectlyrics.server.domain.comment.repository.impl;

import com.projectlyrics.server.domain.comment.domain.Comment;
import com.projectlyrics.server.domain.comment.repository.CommentQueryRepository;
import com.projectlyrics.server.domain.common.util.QueryDslUtils;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.projectlyrics.server.domain.comment.domain.QComment.comment;

@Repository
@RequiredArgsConstructor
public class QueryDslCommentQueryRepository implements CommentQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Comment> findById(Long id) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(comment)
                        .leftJoin(comment.writer).fetchJoin()
                        .leftJoin(comment.note).fetchJoin()
                        .where(
                                comment.id.eq(id),
                                comment.deletedAt.isNull()
                        )
                        .fetchOne()
        );
    }

    @Override
    public Slice<Comment> findAllByNoteId(Long noteId, Long cursorId, Pageable pageable) {
        List<Comment> content = jpaQueryFactory
                .selectFrom(comment)
                .leftJoin(comment.writer).fetchJoin()
                .leftJoin(comment.note).fetchJoin()
                .where(
                        comment.note.id.eq(noteId),
                        comment.deletedAt.isNull(),
                        QueryDslUtils.gtCursorId(cursorId, comment.id)
                )
                .orderBy(comment.id.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return new SliceImpl<>(content, pageable, QueryDslUtils.checkIfHasNext(pageable, content));
    }

    @Override
    public long countByNoteId(Long noteId) {
        return jpaQueryFactory
                .select(comment.count())
                .from(comment)
                .where(
                        comment.note.id.eq(noteId),
                        comment.deletedAt.isNull()
                )
                .fetchOne();
    }
}
