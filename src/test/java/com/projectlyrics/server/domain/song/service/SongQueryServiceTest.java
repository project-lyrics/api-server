package com.projectlyrics.server.domain.song.service;


import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.artist.repository.ArtistCommandRepository;
import com.projectlyrics.server.domain.common.dto.util.CursorBasePaginatedResponse;
import com.projectlyrics.server.domain.song.dto.request.SongCreateRequest;
import com.projectlyrics.server.domain.song.dto.response.SongGetResponse;
import com.projectlyrics.server.domain.song.entity.Song;
import com.projectlyrics.server.support.IntegrationTest;
import com.projectlyrics.server.support.fixture.ArtistFixture;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class SongQueryServiceTest extends IntegrationTest {

    @Autowired
    ArtistCommandRepository artistCommandRepository;

    @Autowired
    SongCommandService songCommandService;

    @Autowired
    SongQueryService sut;

    @ValueSource(strings = {"no", "PAIN"})
    @ParameterizedTest
    void 검색어를_기반으로_제목이_일치하는_곡을_조회해야_한다() throws Exception {
        // given
        Artist artist = artistCommandRepository.save(ArtistFixture.create());
        SongCreateRequest request = new SongCreateRequest(
                1L,
                "spotifyId",
                "No Pain",
                LocalDate.now(),
                "albumName",
                "imageUrl",
                artist.getId()
        );
        Song song = songCommandService.create(request);

        // when
        CursorBasePaginatedResponse<SongGetResponse> result = sut.searchSongs("no", null, 5);

        // then
        assertAll(
                () -> assertThat(result.data().size()).isEqualTo(1),
                () -> assertThat(result.data().getFirst().id()).isEqualTo(song.getId()),
                () -> assertThat(result.data().getFirst().name()).isEqualTo(song.getName()),
                () -> assertThat(result.data().getFirst().imageUrl()).isEqualTo(song.getImageUrl()),
                () -> assertThat(result.data().getFirst().artist().id()).isEqualTo(artist.getId()),
                () -> assertThat(result.data().getFirst().artist().name()).isEqualTo(artist.getName()),
                () -> assertThat(result.data().getFirst().artist().imageUrl()).isEqualTo(artist.getImageUrl())
        );
    }
}