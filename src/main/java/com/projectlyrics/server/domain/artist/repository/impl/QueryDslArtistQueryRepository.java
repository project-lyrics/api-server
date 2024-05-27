package com.projectlyrics.server.domain.artist.repository.impl;

import static com.querydsl.core.types.dsl.Expressions.anyOf;

import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.artist.entity.QArtist;
import com.projectlyrics.server.domain.artist.repository.ArtistQueryRepository;
import com.projectlyrics.server.domain.common.util.QueryDslUtils;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
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
  public Optional<Artist> findByIdAndNotDeleted(Long artistId) {
    return Optional.ofNullable(
        jpaQueryFactory
            .selectFrom(QArtist.artist)
            .where(
                QArtist.artist.id.eq(artistId),
                QArtist.artist.commonField.deletedAt.isNull()
            )
            .fetchOne());
  }

  @Override
  public Slice<Artist> findAllAndNotDeleted(Long cursor, Pageable pageable) {
    var content = jpaQueryFactory
        .selectFrom(QArtist.artist)
        .where(
            goeCursorId(cursor),
            QArtist.artist.commonField.deletedAt.isNull()
        )
        .limit(pageable.getPageSize() + 1)
        .fetch();

    return new SliceImpl<>(content, pageable, QueryDslUtils.checkIfHasNext(pageable, content));
  }

  @Override
  public Slice<Artist> findAllByQueryAndNotDeleted(String query, Long cursor, Pageable pageable) {
    var content = jpaQueryFactory
        .selectFrom(QArtist.artist)
        .where(
            goeCursorId(cursor),
            QArtist.artist.commonField.deletedAt.isNull(),
            anyOf(
                QArtist.artist.name.contains(query),
                QArtist.artist.englishName.contains(query))
        )
        .limit(pageable.getPageSize() + 1)
        .fetch();

    return new SliceImpl<>(content, pageable, QueryDslUtils.checkIfHasNext(pageable, content));
  }

  private BooleanExpression goeCursorId(Long cursor) {
    return cursor == null ? null : QArtist.artist.id.goe(cursor);
  }
}
