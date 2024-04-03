package com.projectlyrics.server.domain.artist.application.persistence.query;

import com.projectlyrics.server.domain.artist.entity.ArtistEntity;
import java.util.Optional;

public interface QueryArtistRepository {

  Optional<ArtistEntity> findById(long id);

}
