package com.projectlyrics.server.domain.song.dto.response;

import com.projectlyrics.server.domain.artist.dto.response.ArtistGetResponse;
import com.projectlyrics.server.domain.common.dto.util.CursorResponse;
import com.projectlyrics.server.domain.song.entity.Song;

public record SongSearchResponse(
        Long id,
        String name,
        String imageUrl,
        long noteCount,
        ArtistGetResponse artist
) implements CursorResponse {

    public static SongSearchResponse from(Song song) {
        return new SongSearchResponse(
                song.getId(),
                song.getName(),
                song.getImageUrl(),
                song.getNotes().size(),
                ArtistGetResponse.of(song.getArtist())
        );
    }

    @Override
    public long getId() {
        return id;
    }
}
