package com.projectlyrics.server.domain.song.repository.impl;

import com.projectlyrics.server.domain.artist.dto.response.ArtistGetResponse;
import com.projectlyrics.server.domain.common.util.QueryDslUtils;
import com.projectlyrics.server.domain.song.dto.response.SongSearchResponse;
import com.projectlyrics.server.domain.song.entity.Song;
import com.projectlyrics.server.domain.song.repository.SongQueryRepository;
import com.querydsl.core.types.ConstructorExpression;
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
    private static final ConstructorExpression<SongSearchResponse> songSearchResponse = Projections.constructor(
            SongSearchResponse.class,
            song.id,
            song.name,
            song.imageUrl,
            note.id.count(),
            Projections.constructor(
                    ArtistGetResponse.class,
                    song.artist.id,
                    song.artist.name,
                    song.artist.imageUrl
            )
    );

    @Override
    public Slice<SongSearchResponse> findAllByQueryOrderByNoteCountDesc(String query, Pageable pageable) {
        List<SongSearchResponse> content = jpaQueryFactory
                .select(songSearchResponse)
                .from(song)
                .leftJoin(song.notes, note)
                .where(
                        songNameContains(query),
                        note.deletedAt.isNull()
                )
                .groupBy(song.id)
                .orderBy(note.id.count().desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return new SliceImpl<>(content, pageable, QueryDslUtils.checkIfHasNext(pageable, content));
    }

    @Override
    public Slice<Song> findAllByQueryAndArtistId(Long artistId, String query, Long cursor, Pageable pageable) {
        List<Song> content = jpaQueryFactory
                .selectFrom(song)
                .leftJoin(song.notes, note)
                .where(
                        songNameContains(query),
                        artistIdEq(artistId),
                        QueryDslUtils.gtCursorId(cursor, song.id)
                )
                .orderBy(song.id.desc())
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
    public Optional<SongSearchResponse> findById(Long id) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .select(songSearchResponse)
                        .from(song)
                        .leftJoin(song.notes, note)
                        .where(
                                song.id.eq(id),
                                note.deletedAt.isNull()
                        )
                        .fetchOne()
        );
    }
}
