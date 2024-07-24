package com.projectlyrics.server.domain.song.entity;

import com.projectlyrics.server.domain.artist.entity.Artist;

import java.time.LocalDate;

public record SongCreate(
        String spotifyId,
        String name,
        LocalDate releaseDate,
        String albumName,
        String imageUrl,
        Artist artist
) {
}
