package com.projectlyrics.server.domain.artist.repository;


import com.projectlyrics.server.domain.artist.entity.Artist;
import java.util.Optional;
import org.springframework.data.repository.Repository;

public interface ArtistRepository extends Repository<Artist, Long> {

  Artist save(Artist entity);

  Optional<Artist> findByIdAndCommonField_DeletedAtIsNull(Long artistId);
}
