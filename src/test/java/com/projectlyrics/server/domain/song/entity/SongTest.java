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
                1L,
                "24ebPi6UpTNw2vdzxGbO9n",
                "Flying Bob",
                LocalDate.parse("2022-09-15"),
                "TEEN TROUBLES",
                "https://i.scdn.co/image/ab67616d0000b2739c3a4e471c5e82a457dce2c0",
                ArtistFixture.create()
        );

        // when
        Song song = Song.create(songCreate);

        // then
        assertAll(
                () -> assertThat(song.getId()).isEqualTo(songCreate.id()),
                () -> assertThat(song.getSpotifyId()).isEqualTo(songCreate.spotifyId()),
                () -> assertThat(song.getName()).isEqualTo(songCreate.name()),
                () -> assertThat(song.getReleaseDate()).isEqualTo(songCreate.releaseDate()),
                () -> assertThat(song.getAlbumName()).isEqualTo(songCreate.albumName()),
                () -> assertThat(song.getImageUrl()).isEqualTo(songCreate.imageUrl()),
                () -> assertThat(song.getArtist()).isEqualTo(songCreate.artist())
        );
    }
}