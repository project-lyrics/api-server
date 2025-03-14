package com.projectlyrics.server.domain.song.repository;

import com.projectlyrics.server.domain.song.entity.Song;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface SongQueryRepository {

    Optional<Song> findById(Long id);
    Optional<Song> findBySpotifyId(String spotifyId);
    Slice<Song> findAllOrderByNoteCountDesc(Pageable pageable);
    Slice<Song> findAllByQueryAndArtistId(Long artistId, String query, Long cursor, Pageable pageable);
    List<Song> findAll();
    List<Song> findAllByIds(List<Long> ids);
}
