package com.projectlyrics.server.domain.song.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.artist.repository.ArtistCommandRepository;
import com.projectlyrics.server.domain.song.dto.request.SongCreateRequest;
import com.projectlyrics.server.domain.song.entity.Song;
import com.projectlyrics.server.domain.song.repository.SongQueryRepository;
import com.projectlyrics.server.support.IntegrationTest;
import com.projectlyrics.server.support.fixture.ArtistFixture;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

class SongCommandServiceTest extends IntegrationTest {

    @Autowired
    ArtistCommandRepository artistCommandRepository;

    @Autowired
    SongQueryRepository songQueryRepository;

    @Autowired
    SongCommandService sut;

    @Test
    void 곡을_저장해야_한다() throws Exception {

        // given
        Artist artist = artistCommandRepository.save(ArtistFixture.create());
        SongCreateRequest request = new SongCreateRequest(
                1L,
                "spotifyId",
                "name",
                LocalDate.EPOCH,
                "albumName",
                "imageUrl",
                artist.getId()
        );

        // when
        Song song = sut.create(request);

        // then
        Slice<Song> result = songQueryRepository.findAllByQueryOrderByNoteCountDesc(null, PageRequest.ofSize(5));
        assertAll(
                () -> assertThat(result.getContent().size()).isEqualTo(1),
                () -> assertThat(result.getContent().getFirst().getId()).isEqualTo(song.getId()),
                () -> assertThat(result.getContent().getFirst().getName()).isEqualTo(song.getName()),
                () -> assertThat(result.getContent().getFirst().getImageUrl()).isEqualTo(song.getImageUrl()),
                () -> assertThat(result.getContent().getFirst().getArtist().getId()).isEqualTo(song.getArtist().getId())
        );
    }
}