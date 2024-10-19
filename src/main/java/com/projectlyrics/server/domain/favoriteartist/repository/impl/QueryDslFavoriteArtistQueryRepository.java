package com.projectlyrics.server.domain.favoriteartist.repository.impl;

import com.projectlyrics.server.domain.common.util.QueryDslUtils;
import com.projectlyrics.server.domain.favoriteartist.entity.FavoriteArtist;
import com.projectlyrics.server.domain.favoriteartist.repository.FavoriteArtistQueryRepository;
import com.projectlyrics.server.domain.user.entity.QUser;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.projectlyrics.server.domain.artist.entity.QArtist.artist;
import static com.projectlyrics.server.domain.favoriteartist.entity.QFavoriteArtist.favoriteArtist;
import static com.projectlyrics.server.domain.note.entity.QNote.note;
import static com.projectlyrics.server.domain.song.entity.QSong.song;

@Repository
@RequiredArgsConstructor
public class QueryDslFavoriteArtistQueryRepository implements FavoriteArtistQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Slice<FavoriteArtist> findAllByUserId(Long userId, Long cursorId, Pageable pageable) {
        List<FavoriteArtist> content = jpaQueryFactory.selectFrom(favoriteArtist)
                .where(
                        favoriteArtist.user.id.eq(userId),
                        favoriteArtist.deletedAt.isNull(),
                        QueryDslUtils.gtCursorId(cursorId, favoriteArtist.id)
                )
                .leftJoin(favoriteArtist.user, QUser.user)
                .fetchJoin()
                .leftJoin(favoriteArtist.artist, artist)
                .fetchJoin()
                .orderBy(favoriteArtist.id.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();
        return new SliceImpl<>(content, pageable, QueryDslUtils.checkIfHasNext(pageable, content));
    }

    @Override
    public List<FavoriteArtist> findAllByUserIdFetchArtist(Long userId) {
        return jpaQueryFactory.selectFrom(favoriteArtist)
                .where(
                        favoriteArtist.user.id.eq(userId),
                        favoriteArtist.deletedAt.isNull()
                )
                .leftJoin(favoriteArtist.artist, artist)
                .fetchJoin()
                .fetch();
    }

    @Override
    public Optional<FavoriteArtist> findByUserIdAndArtistId(Long userId, Long artistId) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(favoriteArtist)
                        .where(
                                favoriteArtist.user.id.eq(userId),
                                favoriteArtist.artist.id.eq(artistId),
                                favoriteArtist.deletedAt.isNull()
                        )
                        .fetchOne()
        );
    }

    @Override
    public List<FavoriteArtist> findAllHavingNotesOfUser(Long userId) {
        return jpaQueryFactory
                .selectFrom(favoriteArtist)
                .join(favoriteArtist.artist, artist).fetchJoin()
                .join(song).on(song.artist.id.eq(artist.id))
                .join(song.notes, note)
                .where(
                        note.publisher.id.eq(userId),
                        favoriteArtist.deletedAt.isNull()
                )
                .orderBy(favoriteArtist.id.desc())
                .fetch();
    }
}
