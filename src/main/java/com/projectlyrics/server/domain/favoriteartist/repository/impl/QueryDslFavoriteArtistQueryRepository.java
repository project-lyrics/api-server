package com.projectlyrics.server.domain.favoriteartist.repository.impl;

import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.artist.entity.QArtist;
import com.projectlyrics.server.domain.common.util.QueryDslUtils;
import com.projectlyrics.server.domain.favoriteartist.entity.FavoriteArtist;
import com.projectlyrics.server.domain.favoriteartist.entity.QFavoriteArtist;
import com.projectlyrics.server.domain.favoriteartist.repository.FavoriteArtistQueryRepository;
import com.projectlyrics.server.domain.user.entity.QUser;
import com.projectlyrics.server.domain.user.entity.User;
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
public class QueryDslFavoriteArtistQueryRepository implements FavoriteArtistQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Slice<FavoriteArtist> findAllByUserId(Long userId, Long cursorId, Pageable pageable) {
        List<FavoriteArtist> content = jpaQueryFactory.selectFrom(QFavoriteArtist.favoriteArtist)
                .where(
                        QFavoriteArtist.favoriteArtist.user.id.eq(userId),
                        QFavoriteArtist.favoriteArtist.deletedAt.isNull(),
                        QueryDslUtils.gtCursorId(cursorId, QFavoriteArtist.favoriteArtist.id)
                )
                .leftJoin(QFavoriteArtist.favoriteArtist.user, QUser.user)
                .fetchJoin()
                .leftJoin(QFavoriteArtist.favoriteArtist.artist, QArtist.artist)
                .fetchJoin()
                .limit(pageable.getPageSize() + 1)
                .fetch();
        return new SliceImpl<>(content, pageable, QueryDslUtils.checkIfHasNext(pageable, content));
    }

    @Override
    public List<FavoriteArtist> findAllByUserIdFetchArtist(Long userId) {
        return jpaQueryFactory.selectFrom(QFavoriteArtist.favoriteArtist)
                .where(
                        QFavoriteArtist.favoriteArtist.user.id.eq(userId),
                        QFavoriteArtist.favoriteArtist.deletedAt.isNull()
                )
                .leftJoin(QFavoriteArtist.favoriteArtist.artist, QArtist.artist)
                .fetchJoin()
                .fetch();
    }

}
