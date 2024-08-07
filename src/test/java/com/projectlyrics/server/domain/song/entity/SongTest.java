package com.projectlyrics.server.domain.song.entity;

import com.projectlyrics.server.support.fixture.ArtistFixture;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class SongTest {

    @Test
    void SongCreate으로부터_Song_객체를_생성해야_한다() throws Exception {
        // given
        SongCreate songCreate = new SongCreate(
                "spotifyId",
                "name",
                LocalDate.now(),
                "albumName",
                "imageUrl",
                ArtistFixture.create()
        );

        // when
        Song song = Song.create(songCreate);

        // then
        assertAll(
                () -> assertThat(song.getId()).isNull(),
                () -> assertThat(song.getSpotifyId()).isEqualTo(songCreate.spotifyId()),
                () -> assertThat(song.getName()).isEqualTo(songCreate.name()),
                () -> assertThat(song.getReleaseDate()).isEqualTo(songCreate.releaseDate()),
                () -> assertThat(song.getAlbumName()).isEqualTo(songCreate.albumName()),
                () -> assertThat(song.getImageUrl()).isEqualTo(songCreate.imageUrl()),
                () -> assertThat(song.getArtist()).isEqualTo(songCreate.artist())
        );
    }

    @Test
    void id와_SongCreate_으로부터_Song_객체를_생성해야_한다() throws Exception {
        // given
        Long id = 1L;
        SongCreate songCreate = new SongCreate(
                "spotifyId",
                "name",
                LocalDate.now(),
                "albumName",
                "imageUrl",
                ArtistFixture.create()
        );

        // when
        Song song = Song.createWithId(id, songCreate);

        // then
        assertAll(
                () -> assertThat(song.getId()).isEqualTo(id),
                () -> assertThat(song.getSpotifyId()).isEqualTo(songCreate.spotifyId()),
                () -> assertThat(song.getName()).isEqualTo(songCreate.name()),
                () -> assertThat(song.getReleaseDate()).isEqualTo(songCreate.releaseDate()),
                () -> assertThat(song.getAlbumName()).isEqualTo(songCreate.albumName()),
                () -> assertThat(song.getImageUrl()).isEqualTo(songCreate.imageUrl()),
                () -> assertThat(song.getArtist()).isEqualTo(songCreate.artist())
        );
    }
}