package com.projectlyrics.server.support.fixture;

import com.projectlyrics.server.domain.artist.entity.Artist;

public class ArtistFixture extends BaseFixture {

    public static Artist create() {
        return Artist.withId(
                getUniqueId(),
                "검정치마",
                "The Black Skirts",
                null,
                "6WeDO4GynFmK4OxwkBzMW8",
                "https://i.scdn.co/image/ab6761610000e5eb8609536d21beed6769d09d7f"
        );
    }

    public static Artist create(Long id) {
        return Artist.withId(
                id,
                "검정치마",
                "The Black Skirts",
                null,
                "6WeDO4GynFmK4OxwkBzMW8",
                "https://i.scdn.co/image/ab6761610000e5eb8609536d21beed6769d09d7f"
        );
    }

    public static Artist create(String name) {
        return Artist.withId(
                getUniqueId(),
                name,
                "The Black Skirts",
                null,
                "6WeDO4GynFmK4OxwkBzMW8",
                "https://i.scdn.co/image/ab6761610000e5eb8609536d21beed6769d09d7f"
        );
    }

    public static Artist create(Long id, String name) {
        return Artist.withId(
                id,
                name,
                "The Black Skirts",
                null,
                "6WeDO4GynFmK4OxwkBzMW8",
                "https://i.scdn.co/image/ab6761610000e5eb8609536d21beed6769d09d7f"
        );
    }
}
