package com.projectlyrics.server.domain.artist.repository;

import com.projectlyrics.server.domain.artist.dto.response.ArtistGetResponse;
import com.projectlyrics.server.domain.artist.entity.Artist;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ArtistQueryRepository {

    Optional<Artist> findById(Long artistId);

    Slice<Artist> findAllByQuery(String query, Long cursor, Pageable pageable);

    Slice<ArtistGetResponse> findAll(Long cursor, int size);

    List<Artist> findAllByIds(List<Long> artistIds);
}
