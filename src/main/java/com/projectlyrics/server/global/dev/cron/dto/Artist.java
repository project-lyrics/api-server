package com.projectlyrics.server.global.dev.cron.dto;

public class Artist {

    private Long id;
    private String spotifyId;

    public Artist(Long id, String spotifyId) {
        this.id = id;
        this.spotifyId = spotifyId;
    }

    public Long getId() {
        return id;
    }

    public String getSpotifyId() {
        return spotifyId;
    }
}
