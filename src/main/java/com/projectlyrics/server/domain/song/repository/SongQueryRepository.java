package com.projectlyrics.server.domain.song.repository;

import com.projectlyrics.server.domain.song.dto.response.SongSearchResponse;
import com.projectlyrics.server.domain.song.entity.Song;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Optional;

public interface SongQueryRepository {

    Slice<SongSearchResponse> findAllByQueryAndArtistId(Long artistId, String query, Pageable pageable);

    Optional<Song> findById(Long id);
}
