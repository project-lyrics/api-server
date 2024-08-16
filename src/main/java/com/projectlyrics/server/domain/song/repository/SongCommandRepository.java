package com.projectlyrics.server.domain.song.repository;

import com.projectlyrics.server.domain.song.entity.Song;

import java.util.List;

public interface SongCommandRepository {

    Song save(Song song);
    void saveAll(List<Song> songs);
}
