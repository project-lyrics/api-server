package com.projectlyrics.server.domain.artist.repository;

import com.projectlyrics.server.domain.artist.entity.Artist;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface QueryArtistRepository {

  Optional<Artist> findByIdAndNotDeleted(Long artistId);

  Slice<Artist> findAllAndNotDeleted(Long cursor, Pageable pageable);
}
