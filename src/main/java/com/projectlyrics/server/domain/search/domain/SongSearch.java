package com.projectlyrics.server.domain.search.domain;

import com.projectlyrics.server.domain.song.entity.Song;

public record SongSearch(
        Long id,
        String title,
        String artistName
) {
    public static SongSearch from(Song song) {
        return new SongSearch(
                song.getId(),
                song.getName(),
                song.getArtist().getName()
        );
    }
}
