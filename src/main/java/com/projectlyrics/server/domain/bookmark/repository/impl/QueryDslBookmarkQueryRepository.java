package com.projectlyrics.server.domain.bookmark.repository.impl;

import com.projectlyrics.server.domain.bookmark.domain.Bookmark;
import com.projectlyrics.server.domain.bookmark.repository.BookmarkQueryRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.projectlyrics.server.domain.bookmark.domain.QBookmark.bookmark;

@Repository
@RequiredArgsConstructor
public class QueryDslBookmarkQueryRepository implements BookmarkQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Bookmark> findByNoteIdAndUserId(Long noteId, Long userId) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(bookmark)
                        .where(
                                bookmark.note.id.eq(noteId),
                                bookmark.user.id.eq(userId),
                                bookmark.deletedAt.isNull()
                        )
                        .fetchOne()
        );
    }
}
