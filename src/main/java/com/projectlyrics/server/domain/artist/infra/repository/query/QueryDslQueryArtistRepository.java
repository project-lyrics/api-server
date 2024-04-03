package com.projectlyrics.server.domain.artist.infra.repository.query;

import com.projectlyrics.server.domain.artist.application.persistence.query.QueryArtistRepository;
import com.projectlyrics.server.domain.artist.entity.ArtistEntity;
import com.projectlyrics.server.domain.artist.entity.QArtistEntity;
import com.projectlyrics.server.domain.common.entity.enumerate.EntityStatusEnum;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class QueryDslQueryArtistRepository implements QueryArtistRepository {

  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public Optional<ArtistEntity> findById(long id) {
    QArtistEntity artist = QArtistEntity.artistEntity;
    return Optional.ofNullable(jpaQueryFactory.selectFrom(artist)
        .where(artist.id.eq(id))
        .where(artist.commonField.status.eq(EntityStatusEnum.IN_USE))
        .fetchFirst());
  }
}
