package com.projectlyrics.server.domain.artist.repository.impl;

import static com.projectlyrics.server.domain.artist.entity.QArtist.*;
import static com.querydsl.core.types.dsl.Expressions.anyOf;

import com.projectlyrics.server.domain.artist.dto.response.ArtistGetResponse;
import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.artist.repository.ArtistQueryRepository;
import com.projectlyrics.server.domain.common.util.QueryDslUtils;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QueryDslArtistQueryRepository implements ArtistQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    private static final ConstructorExpression<ArtistGetResponse> artistGetResponse = Projections.constructor(
            ArtistGetResponse.class,
            artist.id,
            artist.name,
            artist.imageUrl
    );

    @Override
    public Optional<Artist> findById(Long artistId) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(artist)
                        .where(
                                artist.id.eq(artistId),
                                artist.deletedAt.isNull()
                        )
                        .fetchOne());
    }

    @Override
    public Slice<ArtistGetResponse> findAll(Long cursor, int size) {
        List<ArtistGetResponse> content = jpaQueryFactory
                .select(artistGetResponse)
                .from(artist)
                .where(
                        QueryDslUtils.gtCursorId(cursor, artist.id),
                        artist.deletedAt.isNull()
                )
                .limit(size + 1)
                .fetch();

        return new SliceImpl<>(content, PageRequest.ofSize(size), QueryDslUtils.checkIfHasNext(size, content));
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
                                artist.name.contains(query)
                        )
                )
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return new SliceImpl<>(content, pageable, QueryDslUtils.checkIfHasNext(pageable, content));
    }
}
