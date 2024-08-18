package com.projectlyrics.server.domain.song.entity;

import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.song.dto.request.SongCreateRequest;
import com.projectlyrics.server.global.exception.DomainNullFieldException;
import com.projectlyrics.server.support.fixture.ArtistFixture;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class SongCreateTest {

    @Test
    void 요청_파라미터와_아티스트_객체로부터_곡_생성_객체를_생성해야_한다() {
        // given
        Artist artist = ArtistFixture.create();
        SongCreateRequest request = new SongCreateRequest(
                1L,
                "spotifyId",
                "name",
                LocalDate.now(),
                "albumName",
                "imageUrl",
                artist.getId()
        );

        // when
        SongCreate result = SongCreate.from(request, artist);

        // then
        assertAll(
                () -> assertThat(result.spotifyId()).isEqualTo(request.spotifyId()),
                () -> assertThat(result.name()).isEqualTo(request.name()),
                () -> assertThat(result.releaseDate()).isEqualTo(request.releaseDate()),
                () -> assertThat(result.albumName()).isEqualTo(request.albumName()),
                () -> assertThat(result.imageUrl()).isEqualTo(request.imageUrl()),
                () -> assertThat(result.artist()).isEqualTo(artist)
        );
    }

    @Test
    void 널_아티스트에_대해_예외를_발생시켜야_한다() {
        // given
        Artist artist = null;
        SongCreateRequest request = new SongCreateRequest(
                1L,
                "spotifyId",
                "name",
                LocalDate.now(),
                "albumName",
                "imageUrl",
                1L
        );

        // when & then
        assertThatThrownBy(() -> SongCreate.from(request, artist))
                .isInstanceOf(DomainNullFieldException.class);
    }
}