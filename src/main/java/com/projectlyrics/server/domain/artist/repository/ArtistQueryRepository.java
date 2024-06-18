package com.projectlyrics.server.domain.artist.repository;

import com.projectlyrics.server.domain.artist.entity.Artist;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ArtistQueryRepository {

    Optional<Artist> findByIdAndNotDeleted(Long artistId);

    Slice<Artist> findAllByQueryAndNotDeleted(String query, Long cursor, Pageable pageable);

    Slice<Artist> findAllAndNotDeleted(Long cursor, Pageable pageable);
}
