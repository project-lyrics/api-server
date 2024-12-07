package com.projectlyrics.server.global.dev.cron.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TrackListResponse {

    private List<Track> items;
    private String next;

    public List<Track> getItems() {
        return items;
    }

    public String getNext() {
        return next;
    }
}
