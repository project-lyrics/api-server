package com.projectlyrics.server.domain.artist.repository.impl;

import static com.projectlyrics.server.domain.artist.entity.QArtist.*;
import static com.querydsl.core.types.dsl.Expressions.anyOf;

import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.artist.repository.ArtistQueryRepository;
import com.projectlyrics.server.domain.common.util.QueryDslUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.CaseBuilder;
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
    private static final OrderSpecifier[] artistNameOrder = {
            new CaseBuilder()
                    .when(artist.name.between("가", "힣")).then(1)
                    .when(artist.name.between("A", "Z")).then(2)
                    .when(artist.name.between("a", "z")).then(2)
                    .otherwise(3)
                    .asc(),
            artist.name.asc()
    };

    @Override
    public Optional<Artist> findById(Long artistId) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(artist)
                        .where(
                                artist.id.eq(artistId),
                                artist.deletedAt.isNull()
                        )
                        .fetchOne()
        );
    }

    @Override
    public Slice<Artist> findAll(Long cursor, Pageable pageable) {
        List<Artist> content = jpaQueryFactory
                .selectFrom(artist)
                .where(
                        QueryDslUtils.gtCursorId(cursor, artist.id),
                        artist.deletedAt.isNull()
                )
                .limit(pageable.getPageSize() + 1)
                .orderBy(artistNameOrder)
                .fetch();

        return new SliceImpl<>(content, pageable, QueryDslUtils.checkIfHasNext(pageable, content));
    }

    @Override
    public List<Artist> findAllByIds(List<Long> artistIds) {
        return jpaQueryFactory.selectFrom(artist)
                .where(
                        artist.id.in(artistIds),
                        artist.deletedAt.isNull()
                )
                .fetch();
    }

    @Override
    public Slice<Artist> findAllByQuery(String query, Long cursor, Pageable pageable) {
        List<Artist> content = jpaQueryFactory
                .selectFrom(artist)
                .where(
                        QueryDslUtils.gtCursorId(cursor, artist.id),
                        artist.deletedAt.isNull(),
                        anyOf(
                                artist.name.containsIgnoreCase(query),
                                artist.secondName.containsIgnoreCase(query),
                                artist.thirdName.containsIgnoreCase(query)
                        )
                )
                .limit(pageable.getPageSize() + 1)
                .orderBy(artistNameOrder)
                .fetch();

        return new SliceImpl<>(content, pageable, QueryDslUtils.checkIfHasNext(pageable, content));
    }
}
