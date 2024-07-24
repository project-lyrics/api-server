package com.projectlyrics.server.domain.song.repository.impl;

import com.projectlyrics.server.domain.common.util.QueryDslUtils;
import com.projectlyrics.server.domain.song.entity.QSong;
import com.projectlyrics.server.domain.song.entity.Song;
import com.projectlyrics.server.domain.song.repository.SongQueryRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class QueryDslSongQueryRepository implements SongQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Slice<Song> findAllByArtistId(Long artistId, Long cursorId, Pageable pageable) {
        List<Song> content = jpaQueryFactory
                .selectFrom(QSong.song)
                .where(
                        QSong.song.artist.id.eq(artistId),
                        QSong.song.deletedAt.isNull(),
                        QueryDslUtils.gtCursorId(cursorId, QSong.song.id)
                )
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return new SliceImpl<>(content, pageable, QueryDslUtils.checkIfHasNext(pageable, content));
    }

    @Override
    public Slice<Song> findAllByQuery(String query, Long cursorId, Pageable pageable) {
        List<Song> content = jpaQueryFactory
                .selectFrom(QSong.song)
                .where(
                        QSong.song.name.containsIgnoreCase(query),
                        QSong.song.deletedAt.isNull(),
                        QueryDslUtils.gtCursorId(cursorId, QSong.song.id)
                )
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return new SliceImpl<>(content, pageable, QueryDslUtils.checkIfHasNext(pageable, content));
    }

    @Override
    public Optional<Song> findById(Long id) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(QSong.song)
                        .where(
                                QSong.song.id.eq(id),
                                QSong.song.deletedAt.isNull()
                        )
                        .fetchOne()
        );
    }
}
