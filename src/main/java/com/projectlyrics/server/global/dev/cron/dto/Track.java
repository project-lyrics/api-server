package com.projectlyrics.server.global.dev.cron.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Track {

    private String id;
    private String name;
    private int track_number;
    private List<ArtistOfTrack> artists;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getTrack_number() {
        return track_number;
    }

    public List<ArtistOfTrack> getArtists() {
        return artists;
    }
}
