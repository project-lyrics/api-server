package com.projectlyrics.server.domain.artist.repository;

import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.artist.entity.QArtist;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

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
}
