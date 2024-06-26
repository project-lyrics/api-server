package com.projectlyrics.server.common.fixture;

import com.projectlyrics.server.domain.artist.dto.request.ArtistAddRequest;
import com.projectlyrics.server.domain.artist.entity.Artist;

public class ArtistFixture {

    public static Artist create() {
        return Artist.from(new ArtistAddRequest(
                "넬",
                "NELL",
                "https://~"
        ));
    }

    public static Artist createWithName(String name) {
        return Artist.from(new ArtistAddRequest(
                name,
                "NELL",
                "https://~"
        ));
    }
}
