package com.projectlyrics.server.domain.song.repository;

import com.projectlyrics.server.domain.song.entity.Song;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface SongQueryRepository {
    Slice<Song> findAllByArtistId(Long artistId, Long cursorId, Pageable pageable);

    Slice<Song> findAllByQuery(String query, Long cursorId, Pageable pageable);
}
