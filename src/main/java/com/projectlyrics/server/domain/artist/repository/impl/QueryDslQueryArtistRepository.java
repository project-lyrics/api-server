package com.projectlyrics.server.domain.artist.repository.impl;

import static com.querydsl.core.types.dsl.Expressions.anyOf;

import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.artist.entity.QArtist;
import com.projectlyrics.server.domain.artist.repository.QueryArtistRepository;
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
public class QueryDslQueryArtistRepository implements QueryArtistRepository {

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

    var hasNext = false;

    if (content.size() > pageable.getPageSize()) {
      content.remove(pageable.getPageSize());
      hasNext = true;
    }

    return new SliceImpl<>(content, pageable, hasNext);
  }

  @Override
  public Slice<Artist> findAllByQueryAndNotDeleted(String query, Long cursor, Pageable pageable) {
    var content = jpaQueryFactory
        .selectFrom(QArtist.artist)
        .where(
            goeCursorId(cursor)
                .and(QArtist.artist.commonField.deletedAt.isNull())
                .and(anyOf(
                    QArtist.artist.name.contains(query),
                    QArtist.artist.englishName.contains(query))
                )
        )
        .limit(pageable.getPageSize() + 1)
        .fetch();

    var hasNext = false;

    if (content.size() > pageable.getPageSize()) {
      content.remove(pageable.getPageSize());
      hasNext = true;
    }

    return new SliceImpl<>(content, pageable, hasNext);
  }

  private BooleanExpression goeCursorId(Long cursor) {
    return cursor == null ? null : QArtist.artist.id.goe(cursor);
  }
}
