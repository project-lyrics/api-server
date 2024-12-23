package com.projectlyrics.server.domain.song.repository.impl;

import static com.projectlyrics.server.domain.artist.entity.QArtist.artist;
import static com.projectlyrics.server.domain.note.entity.QNote.note;
import static com.projectlyrics.server.domain.song.entity.QSong.song;

import com.projectlyrics.server.domain.common.util.QueryDslUtils;
import com.projectlyrics.server.domain.song.entity.Song;
import com.projectlyrics.server.domain.song.repository.SongQueryRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QueryDslSongQueryRepository implements SongQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Song> findById(Long id) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .select(song)
                        .from(song)
                        .leftJoin(song.notes, note)
                        .where(song.id.eq(id))
                        .fetchOne()
        );
    }

    @Override
    public Optional<Song> findBySpotifyId(String spotifyId) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(song)
                        .where(song.spotifyId.eq(spotifyId))
                        .fetchFirst()
        );
    }

    @Override
    public Slice<Song> findAllByQueryOrderByNoteCountDesc(String query, Pageable pageable) {
        List<Song> content = jpaQueryFactory
                .select(song)
                .from(song)
                .leftJoin(song.notes, note)
                .where(
                        songNameContains(query)
                )
                .groupBy(song.id)
                .orderBy(
                        Expressions.numberTemplate(Long.class,
                                        "(select count(n) from Note n where n.song.id = song.id and n.deletedAt is null)")
                                .desc(),
                        song.id.asc()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return new SliceImpl<>(content, pageable, QueryDslUtils.checkIfHasNext(pageable, content));
    }

    @Override
    public Slice<Song> findAllByQueryAndArtistId(Long artistId, String query, Long cursor, Pageable pageable) {
        List<Song> content = jpaQueryFactory
                .selectFrom(song)
                .where(
                        songNameContains(query),
                        artistIdEq(artistId),
                        QueryDslUtils.ltCursorId(cursor, song.id)
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
    public List<Song> findAll() {
        return jpaQueryFactory
                .selectFrom(song)
                .join(song.artist, artist).fetchJoin()
                .fetch();
    }
}
