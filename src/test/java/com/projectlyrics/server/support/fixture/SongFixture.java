package com.projectlyrics.server.support.fixture;

import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.song.entity.Song;
import com.projectlyrics.server.domain.song.entity.SongCreate;

import java.time.LocalDate;

public class SongFixture extends BaseFixture {

    public static Song create() {
        return Song.create(
                new SongCreate(
                        getUniqueId(),
                        "24ebPi6UpTNw2vdzxGbO9n",
                        "Flying Bob",
                        LocalDate.parse("2022-09-15"),
                        "TEEN TROUBLES",
                        "https://i.scdn.co/image/ab67616d0000b2739c3a4e471c5e82a457dce2c0",
                        ArtistFixture.create()
                )
        );
    }

    public static Song create(Artist artist) {
        return Song.create(
                new SongCreate(
                        getUniqueId(),
                        "24ebPi6UpTNw2vdzxGbO9n",
                        "Flying Bob",
                        LocalDate.parse("2022-09-15"),
                        "TEEN TROUBLES",
                        "https://i.scdn.co/image/ab67616d0000b2739c3a4e471c5e82a457dce2c0",
                        artist
                )
        );
    }
}
