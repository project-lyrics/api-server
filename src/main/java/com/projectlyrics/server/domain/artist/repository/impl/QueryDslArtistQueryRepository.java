package com.projectlyrics.server.domain.artist.repository.impl;

import static com.querydsl.core.types.dsl.Expressions.anyOf;

import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.artist.entity.QArtist;
import com.projectlyrics.server.domain.artist.repository.ArtistQueryRepository;
import com.projectlyrics.server.domain.common.util.QueryDslUtils;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QueryDslArtistQueryRepository implements ArtistQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Artist> findById(Long artistId) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(QArtist.artist)
                        .where(
                                QArtist.artist.id.eq(artistId),
                                QArtist.artist.deletedAt.isNull()
                        )
                        .fetchOne());
    }

    @Override
    public Slice<Artist> findAllAndNotDeleted(Long cursor, Pageable pageable) {
        List<Artist> content = jpaQueryFactory
                .selectFrom(QArtist.artist)
                .where(
                        QueryDslUtils.gtCursorId(cursor, QArtist.artist.id),
                        QArtist.artist.deletedAt.isNull()
                )
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return new SliceImpl<>(content, pageable, QueryDslUtils.checkIfHasNext(pageable, content));
    }

    @Override
    public List<Artist> findAllByIds(List<Long> artistIds) {
        return jpaQueryFactory.selectFrom(QArtist.artist)
                .where(
                        QArtist.artist.id.in(artistIds),
                        QArtist.artist.deletedAt.isNull()
                )
                .fetch();
    }

    @Override
    public Slice<Artist> findAllByQueryAndNotDeleted(String query, Long cursor, Pageable pageable) {
        List<Artist> content = jpaQueryFactory
                .selectFrom(QArtist.artist)
                .where(
                        QueryDslUtils.gtCursorId(cursor, QArtist.artist.id),
                        QArtist.artist.deletedAt.isNull(),
                        anyOf(
                                QArtist.artist.name.contains(query)
                        )
                )
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return new SliceImpl<>(content, pageable, QueryDslUtils.checkIfHasNext(pageable, content));
    }
}
