package com.projectlyrics.server.domain.artist.repository;

import static com.projectlyrics.server.domain.artist.entity.QArtist.*;

import com.projectlyrics.server.domain.artist.entity.Artist;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ArtistRepositoryQCustomImpl implements ArtistRepositoryQCustom {

  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public Optional<Artist> findByIdAndNotDeleted(Long artistId) {
    return Optional.ofNullable(
        jpaQueryFactory
            .selectFrom(artist)
            .where(
                artist.id.eq(artistId),
                artist.commonField.deletedAt.isNull()
            )
            .fetchOne());
  }
}
