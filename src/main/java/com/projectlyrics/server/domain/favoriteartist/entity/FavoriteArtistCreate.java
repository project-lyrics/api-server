package com.projectlyrics.server.domain.favoriteartist.entity;

import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.user.entity.User;

public record FavoriteArtistCreate(
        User user,
        Artist artist
) {

    public static FavoriteArtistCreate of(User user, Artist artist) {
        return new FavoriteArtistCreate(user, artist);
    }
}
