package com.projectlyrics.server.domain.artist.repository;


import com.projectlyrics.server.domain.artist.entity.Artist;
import org.springframework.data.repository.Repository;

public interface CommandQueryArtistRepository extends Repository<Artist, Long>, QueryArtistRepository {

  Artist save(Artist entity);
}
