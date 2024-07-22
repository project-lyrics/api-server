package com.projectlyrics.server.support.fixture;

import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.favoriteartist.entity.FavoriteArtist;
import com.projectlyrics.server.domain.user.entity.User;

public class FavoriteArtistFixture extends BaseFixture {

    private Long id;
    private User user;
    private Artist artist;

    public static FavoriteArtist create(User user, Artist artist) {
        return FavoriteArtist.withId(getUniqueId(), user, artist);
    }

    private FavoriteArtistFixture() {}

    public FavoriteArtistFixture builder() {
        return new FavoriteArtistFixture();
    }

    public FavoriteArtistFixture id(Long id) {
        this.id = id;
        return this;
    }

    public FavoriteArtistFixture user(User user) {
        this.user = user;
        return this;
    }

    public FavoriteArtistFixture artist(Artist artist) {
        this.artist = artist;
        return this;
    }

    public FavoriteArtist build() {
        return FavoriteArtist.withId(id, user, artist);
    }
}
