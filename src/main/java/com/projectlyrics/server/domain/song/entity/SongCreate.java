package com.projectlyrics.server.domain.song.entity;

import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.song.dto.request.SongCreateRequest;

import java.time.LocalDate;

import static com.projectlyrics.server.domain.common.util.DomainUtils.checkNull;

public record SongCreate(
        Long id,
        String spotifyId,
        String name,
        LocalDate releaseDate,
        String albumName,
        String imageUrl,
        Artist artist
) {

    public static SongCreate from(SongCreateRequest request, Artist artist) {
        checkNull(artist);

        return new SongCreate(
                null,
                request.spotifyId(),
                request.name(),
                request.releaseDate(),
                request.albumName(),
                request.imageUrl(),
                artist
        );
    }
}
