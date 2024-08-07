package com.projectlyrics.server.domain.song.dto.response;

import com.projectlyrics.server.domain.artist.dto.response.ArtistGetResponse;
import com.projectlyrics.server.domain.common.dto.util.CursorResponse;
import com.projectlyrics.server.domain.song.entity.Song;

public record SongGetResponse(
        Long id,
        String name,
        String imageUrl,
        ArtistGetResponse artist
) implements CursorResponse {

    public static SongGetResponse from(Song song) {
        return new SongGetResponse(
                song.getId(),
                song.getName(),
                song.getImageUrl(),
                ArtistGetResponse.of(song.getArtist())
        );
    }

    @Override
    public long getId() {
        return id;
    }
}
