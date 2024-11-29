package com.projectlyrics.server.global.dev.cron.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AlbumListResponse {

    private String next;
    private List<Album> items;

    public AlbumListResponse() {
    }

    public String getNext() {
        return next;
    }

    public List<Album> getItems() {
        return items;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public void setItems(List<Album> items) {
        this.items = items;
    }
}
