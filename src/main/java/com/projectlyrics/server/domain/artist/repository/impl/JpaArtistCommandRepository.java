package com.projectlyrics.server.domain.artist.repository.impl;

import com.projectlyrics.server.domain.artist.entity.Artist;
import org.springframework.data.repository.Repository;

public interface JpaArtistCommandRepository extends Repository<Artist, Long> {

    Artist save(Artist entity);
}
