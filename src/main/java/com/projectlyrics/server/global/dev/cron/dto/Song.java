package com.projectlyrics.server.global.dev.cron.dto;

public class Song {

    Long id;
    String spotifyId;
    String name;
    String releaseDate;
    String albumName;
    String imageUrl;
    Long artistId;

    public Song(
            Long id,
            String spotifyId,
            String name,
            String releaseDate,
            String albumName,
            String imageUrl,
            Long artistId
    ) {
        this.id = id;
        this.spotifyId = spotifyId;
        this.name = name;
        this.releaseDate = releaseDate;
        this.albumName = albumName;
        this.imageUrl = imageUrl;
        this.artistId = artistId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public static Song of(Album album, Track track) {
        return new Song(
                null,
                track.getId(),
                track.getName(),
                album.getRelease_date(),
                album.getName(),
                album.getImages().get(0).getUrl(),
                null
        );
    }
}
