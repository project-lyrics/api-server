package com.projectlyrics.server.domain.search.domain;

import com.projectlyrics.server.domain.song.entity.Song;

import java.util.List;

public record SongSearch(
        Long id,
        String korean_title,
        String english_title,
        String korean_artist_name,
        String english_artist_name,
        String korean_album_name,
        String english_album_name
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
                isKorean(song.getName()) ? song.getName() : null,
                isKorean(song.getName()) ? null : song.getName(),
                isKorean(song.getArtist().getName()) ? song.getArtist().getName() : null,
                isKorean(song.getArtist().getName()) ? null : song.getArtist().getName(),
                isKorean(song.getAlbumName()) ? song.getAlbumName() : null,
                isKorean(song.getAlbumName()) ? null : song.getAlbumName()
        );
    }

    private static boolean isKorean(String input) {
        String koreanRegex = "^[\\uAC00-\\uD7A3]+$";
        return input.matches(koreanRegex);
    }
}
