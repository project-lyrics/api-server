package com.projectlyrics.server.support.fixture;

import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.song.entity.Song;
import com.projectlyrics.server.domain.song.entity.SongCreate;

import java.time.LocalDate;

public class SongFixture extends BaseFixture {

    private Long id;
    private String spotifyId = "spotifyId";
    private String name = "노래 이름";
    private LocalDate releaseDate = LocalDate.now();
    private String albumName = "앨범 이름";
    private String imageUrl = "https://asdf.com";
    private Artist artist;

    public static Song create(SongCreate songCreate) {
        return Song.createWithId(getUniqueId(), songCreate);
    }

    private SongFixture() {}

    public static SongFixture builder() {
        return new SongFixture();
    }

    public SongFixture id(Long id) {
        this.id = id;
        return this;
    }

    public SongFixture spotifyId(String spotifyId) {
        this.spotifyId = spotifyId;
        return this;
    }

    public SongFixture name(String name) {
        this.name = name;
        return this;
    }

    public SongFixture releaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
        return this;
    }

    public SongFixture albumName(String albumName) {
        this.albumName = albumName;
        return this;
    }

    public SongFixture imageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public SongFixture artist(Artist artist) {
        this.artist = artist;
        return this;
    }

    public Song build() {
        return Song.createWithId(id, new SongCreate(spotifyId, name, releaseDate, albumName, imageUrl, artist));
    }
}
