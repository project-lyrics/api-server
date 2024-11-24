package com.projectlyrics.server.domain.comment.repository.impl;

import com.projectlyrics.server.domain.comment.domain.Comment;
import com.projectlyrics.server.domain.comment.repository.CommentQueryRepository;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.projectlyrics.server.domain.block.domain.QBlock.block;
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
    public List<Comment> findAllByNoteId(Long noteId, Long userId) {
        return jpaQueryFactory
                .selectFrom(comment)
                .where(
                        comment.note.id.eq(noteId),
                        comment.deletedAt.isNull(),
                        comment.writer.id.notIn(
                                JPAExpressions
                                        .select(block.blocked.id)
                                        .from(block)
                                        .where(block.blocker.id.eq(userId))
                        )
                )
                .orderBy(comment.id.asc())
                .fetch();
    }
}
