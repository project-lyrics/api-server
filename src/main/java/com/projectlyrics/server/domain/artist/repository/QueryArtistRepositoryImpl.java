package com.projectlyrics.server.domain.artist.repository;

import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.artist.entity.QArtist;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

@RequiredArgsConstructor
public class QueryArtistRepositoryImpl implements QueryArtistRepository {

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
  public Slice<Artist> findAllAndNotDeleted(Pageable pageable) {
    var content = jpaQueryFactory
        .selectFrom(QArtist.artist)
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize() + 1)
        .fetch();

    var hasNext = false;

    if (content.size() > pageable.getPageSize()) {
      content.remove(pageable.getPageSize());
      hasNext = true;
    }

    return new SliceImpl<>(content, pageable, hasNext);
  }
}
