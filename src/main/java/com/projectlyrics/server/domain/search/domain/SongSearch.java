package com.projectlyrics.server.domain.search.domain;

import com.projectlyrics.server.domain.song.entity.Song;

import java.util.List;

public record SongSearch(
        Long id,
        String title,
        String artistName
) {
    public static final String INDEX = "song";
    public static final List<String> FIELDS = List.of(
            "korean_title",
            "english_title",
            "korean_artist_name",
            "english_artist_name",
            "korean_album_name",
            "english_album_name"
    );

    public static SongSearch from(Song song) {
        return new SongSearch(
                song.getId(),
                song.getName(),
                song.getArtist().getName()
        );
    }
}
