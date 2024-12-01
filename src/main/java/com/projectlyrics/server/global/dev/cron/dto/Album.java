package com.projectlyrics.server.global.dev.cron.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Album {

    private int total_tracks;
    private String id;
    private List<Image> images;
    private String name;
    private String release_date;

    public Album() {
    }

    public Album(String id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return LocalDate.parse(release_date);
    }
}
