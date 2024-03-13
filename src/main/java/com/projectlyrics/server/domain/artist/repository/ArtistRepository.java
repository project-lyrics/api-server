package com.projectlyrics.server.domain.artist.repository;


import com.projectlyrics.server.domain.artist.entity.Artist;
import org.springframework.data.repository.Repository;

public interface ArtistRepository extends Repository<Artist, Long>, ArtistRepositoryQCustom {

  Artist save(Artist entity);
}
