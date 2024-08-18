package com.projectlyrics.server.domain.artist.service;

import com.projectlyrics.server.domain.artist.dto.request.ArtistCreateRequest;
import com.projectlyrics.server.support.IntegrationTest;
import com.projectlyrics.server.support.fixture.ArtistFixture;
import com.projectlyrics.server.domain.artist.dto.request.ArtistUpdateRequest;
import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.artist.exception.ArtistNotFoundException;
import com.projectlyrics.server.domain.artist.repository.ArtistCommandRepository;
import com.projectlyrics.server.domain.artist.repository.ArtistQueryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

class ArtistCommandServiceTest extends IntegrationTest {

    @Autowired
    ArtistCommandService sut;

    @Autowired
    ArtistCommandRepository artistCommandRepository;

    @Autowired
    ArtistQueryRepository artistQueryRepository;

    @Test
    void 새로운_아티스트를_추가한다() {
        // given
        ArtistCreateRequest request = new ArtistCreateRequest(
                "검정치마",
                "The Black Skirts",
                null,
                "6WeDO4GynFmK4OxwkBzMW8",
                "https://i.scdn.co/image/ab6761610000e5eb8609536d21beed6769d09d7f"
        );

        // when
        Artist result = sut.create(request);

        // then
        assertAll(
                () -> assertThat(result.getName()).isEqualTo(request.name()),
                () -> assertThat(result.getSecondName()).isEqualTo(request.secondName()),
                () -> assertThat(result.getThirdName()).isNull(),
                () -> assertThat(result.getSpotifyId()).isEqualTo(request.spotifyId()),
                () -> assertThat(result.getImageUrl()).isEqualTo(request.imageUrl())
        );
    }

    @Test
    void 아티스트를_수정할_때_요청의_null_값은_수정하지_않는다() {
        // given
        Artist artist = artistCommandRepository.save(ArtistFixture.create());
        ArtistUpdateRequest request = new ArtistUpdateRequest(
                "초록불꽃소년단",
                null,
                "Green Flame Boys",
                "https://i.scdn.co/image/ab6761610000e5eb8609536d21beed6769d09d7f"
        );

        // when
        Artist result = sut.update(artist.getId(), request);

        // then
        assertAll(
                () -> assertThat(result.getName()).isEqualTo(request.name()),
                () -> assertThat(result.getSecondName()).isEqualTo(artist.getSecondName()),
                () -> assertThat(result.getThirdName()).isEqualTo(request.thirdName()),
                () -> assertThat(result.getImageUrl()).isEqualTo(request.imageUrl())
        );
    }

    @Test
    void 존재하지_않는_아티스트를_수정할_경우_예외가_발생해야_한다() throws Exception {
        // given
        ArtistUpdateRequest request = new ArtistUpdateRequest(
                "초록불꽃소년단",
                null,
                "Green Flame Boys",
                "https://i.scdn.co/image/ab6761610000e5eb8609536d21beed6769d09d7f"
        );

        // when, then
        assertThatThrownBy(() -> sut.update(99L, request))
                .isInstanceOf(ArtistNotFoundException.class);
    }
}
