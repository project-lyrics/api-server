package com.projectlyrics.server.common.fixture;

import com.projectlyrics.server.domain.artist.dto.request.ArtistAddRequest;
import com.projectlyrics.server.domain.artist.entity.Artist;

public class ArtistFixture {

    private String name = "아티스트 이름";
    private String imageUrl = "https://asdf.com";

    public static Artist create() {
        return Artist.of("아티스트 이름", "https://asdf.com");
    }

    public static Artist createWithName(String name) {
        return Artist.of(name, "https://asdf.com");
    }

    private ArtistFixture() {}

    public static ArtistFixture builder() {
        return new ArtistFixture();
    }

    public ArtistFixture name(String name) {
        this.name = name;
        return this;
    }

    public ArtistFixture imageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public Artist build() {
        return Artist.of(name, imageUrl);
    }
}
