package com.projectlyrics.server.domain.song.service;

import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.artist.repository.ArtistCommandRepository;
import com.projectlyrics.server.domain.song.dto.request.SongCreateRequest;
import com.projectlyrics.server.domain.song.entity.Song;
import com.projectlyrics.server.domain.song.repository.SongQueryRepository;
import com.projectlyrics.server.support.IntegrationTest;
import com.projectlyrics.server.support.fixture.ArtistFixture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class SongCommandServiceTest extends IntegrationTest {

    @Autowired
    ArtistCommandRepository artistCommandRepository;

    @Autowired
    SongQueryRepository songQueryRepository;

    @Autowired
    SongCommandService sut;

    @SpyBean
    Clock clock;

    @Test
    void 곡을_저장해야_한다() throws Exception {
        // given
        Clock releaseDate = Clock.fixed(Instant.parse("2024-07-10T10:00:00Z"), ZoneOffset.UTC);
        Artist artist = artistCommandRepository.save(ArtistFixture.create());
        SongCreateRequest request = new SongCreateRequest(
                "spotifyId",
                "name",
                LocalDate.now(releaseDate),
                "albumName",
                "imageUrl",
                artist.getId()
        );

        // when
        Song song = sut.create(request);

        // then
        Slice<Song> result = songQueryRepository.findAllByArtistId(artist.getId(), null, PageRequest.ofSize(5));
        assertAll(
                () -> assertThat(result.getContent().size()).isEqualTo(1),
                () -> assertThat(result.getContent().getFirst().getId()).isEqualTo(song.getId()),
                () -> assertThat(result.getContent().getFirst().getSpotifyId()).isEqualTo(song.getSpotifyId()),
                () -> assertThat(result.getContent().getFirst().getName()).isEqualTo(song.getName()),
                () -> assertThat(result.getContent().getFirst().getReleaseDate()).isEqualTo(song.getReleaseDate()),
                () -> assertThat(result.getContent().getFirst().getAlbumName()).isEqualTo(song.getAlbumName()),
                () -> assertThat(result.getContent().getFirst().getImageUrl()).isEqualTo(song.getImageUrl()),
                () -> assertThat(result.getContent().getFirst().getArtist().getId()).isEqualTo(song.getArtist().getId())
        );
    }
}