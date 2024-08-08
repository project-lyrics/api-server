package com.projectlyrics.server.domain.comment.repository.impl;

import com.projectlyrics.server.domain.comment.domain.Comment;
import com.projectlyrics.server.domain.comment.repository.CommentQueryRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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
}
