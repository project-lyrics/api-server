package com.projectlyrics.server.domain.song.repository.impl;

import com.projectlyrics.server.domain.artist.dto.response.ArtistGetResponse;
import com.projectlyrics.server.domain.common.util.QueryDslUtils;
import com.projectlyrics.server.domain.song.dto.response.SongSearchResponse;
import com.projectlyrics.server.domain.song.entity.Song;
import com.projectlyrics.server.domain.song.repository.SongQueryRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.projectlyrics.server.domain.note.entity.QNote.note;
import static com.projectlyrics.server.domain.song.entity.QSong.song;

@Repository
@RequiredArgsConstructor
public class QueryDslSongQueryRepository implements SongQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Slice<SongSearchResponse> findAllByQueryAndArtistId(Long artistId, String query, Pageable pageable) {
        List<SongSearchResponse> content = jpaQueryFactory
                .select(Projections.constructor(
                        SongSearchResponse.class,
                        song.id,
                        song.name,
                        song.imageUrl,
                        note.id.count(),
                        Projections.constructor(
                                ArtistGetResponse.class,
                                song.artist.id,
                                song.artist.name,
                                song.artist.imageUrl))
                )
                .from(song)
                .leftJoin(song.notes, note)
                .where(
                        songNameContains(query),
                        artistIdEq(artistId)
                )
                .groupBy(song.id)
                .orderBy(note.id.count().desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return new SliceImpl<>(content, pageable, QueryDslUtils.checkIfHasNext(pageable, content));
    }

    private static BooleanExpression songNameContains(String query) {
        return Objects.isNull(query) || query.isEmpty() ? null : song.name.containsIgnoreCase(query);
    }

    private static BooleanExpression artistIdEq(Long artistId) {
        return Objects.isNull(artistId) ? null : song.artist.id.eq(artistId);
    }

    @Override
    public Optional<Song> findById(Long id) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(song)
                        .join(song.artist).fetchJoin()
                        .where(
                                song.id.eq(id)
                        )
                        .fetchOne()
        );
    }
}
