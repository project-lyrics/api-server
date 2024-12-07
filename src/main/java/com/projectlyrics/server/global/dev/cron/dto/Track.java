package com.projectlyrics.server.global.dev.cron.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Track {

    private String id;
    private String name;
    private int track_number;
    private List<ArtistOfTrack> artists;
}
