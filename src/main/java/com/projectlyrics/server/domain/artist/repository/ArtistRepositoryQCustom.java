package com.projectlyrics.server.domain.artist.repository;

import com.projectlyrics.server.domain.artist.entity.Artist;
import java.util.Optional;

public interface ArtistRepositoryQCustom {

  Optional<Artist> findByIdAndNotDeleted(Long artistId);
}
