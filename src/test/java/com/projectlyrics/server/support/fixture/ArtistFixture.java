package com.projectlyrics.server.support.fixture;

import com.projectlyrics.server.domain.artist.entity.Artist;

import java.util.Optional;

public class ArtistFixture {

    private static long id = 1;
    private String name = "아티스트 이름";
    private String imageUrl = "https://asdf.com";

    public static Artist create() {
        return Artist.withId(id++, "아티스트 이름", "https://asdf.com");
    }

    public static Artist createWithName(String name) {
        return Artist.withId(id++, name, "https://asdf.com");
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
        return Artist.withId(id++, name, imageUrl);
    }
}
