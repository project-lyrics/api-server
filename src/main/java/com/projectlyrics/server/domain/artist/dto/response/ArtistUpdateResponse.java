package com.projectlyrics.server.domain.artist.dto.response;

import com.projectlyrics.server.domain.artist.entity.Artist;

public record ArtistUpdateResponse(
        Long id,
        String name,
        String englishName,
        String profileImageCdnLink
) {

    public static ArtistUpdateResponse from(Artist artist) {
        return new ArtistUpdateResponse(
                artist.getId(),
                artist.getName(),
                artist.getEnglishName(),
                artist.getProfileImageCdnLink()
        );
    }
}
