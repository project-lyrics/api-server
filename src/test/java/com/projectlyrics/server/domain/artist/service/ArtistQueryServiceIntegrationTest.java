package com.projectlyrics.server.domain.artist.service;

import com.projectlyrics.server.domain.common.dto.util.OffsetBasePaginatedResponse;
import com.projectlyrics.server.support.IntegrationTest;
import com.projectlyrics.server.support.fixture.ArtistFixture;
import com.projectlyrics.server.domain.artist.dto.response.ArtistGetResponse;
import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.artist.repository.ArtistCommandRepository;
import com.projectlyrics.server.domain.artist.repository.ArtistQueryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertAll;

class ArtistQueryServiceIntegrationTest extends IntegrationTest {

    @Autowired
    ArtistQueryService sut;

    @Autowired
    ArtistCommandRepository artistCommandRepository;

    @Autowired
    ArtistQueryRepository artistQueryRepository;

    @Test
    void 아티스트_데이터를_조회한다() throws Exception {
        //given
        Artist savedArtist = artistCommandRepository.save(ArtistFixture.create());

        //when
        Artist artist = sut.getArtistById(savedArtist.getId());

        //then
        assertThat(artist).isEqualTo(savedArtist);
    }

    @Test
    void 아티스트의_데이터를_오프셋_기반_페이지네이션으로_조회한다() throws Exception {
        // given
        artistCommandRepository.save(ArtistFixture.create("실리카겔"));
        artistCommandRepository.save(ArtistFixture.create("잔나비"));
        artistCommandRepository.save(ArtistFixture.create("너드커넥션"));

        Pageable pageable = PageRequest.of(0, 3);

        // when
        OffsetBasePaginatedResponse<ArtistGetResponse> response = sut.getArtistList(pageable);

        // then
        assertAll(
                () -> assertThat(response.hasNext()).isFalse(),
                () -> assertThat(response.data()).hasSize(3),
                () -> assertThat(response.data().get(0).name()).isEqualTo("너드커넥션"),
                () -> assertThat(response.data().get(1).name()).isEqualTo("실리카겔"),
                () -> assertThat(response.data().get(2).name()).isEqualTo("잔나비")
        );
    }

    @Test
    void 검색어_기반으로_아티스트의_데이터를_오프셋_기반_페이지네이션으로_조회한다() throws Exception {
        // given
        Artist artist1 = artistCommandRepository.save(ArtistFixture.create("실리카겔"));
        artistCommandRepository.save(ArtistFixture.create("잔나비"));
        artistCommandRepository.save(ArtistFixture.create("너드커넥션"));

        Pageable pageable = PageRequest.of(0, 3);

        // when
        OffsetBasePaginatedResponse<ArtistGetResponse> response = sut.searchArtists("실리", pageable);

        // then
        assertSoftly(s -> {
                    s.assertThat(response.hasNext()).isFalse();
                    s.assertThat(response.data().size()).isEqualTo(1);
                    s.assertThat(response.data().get(0).name()).isEqualTo(artist1.getName());
                }
        );
    }

    @Test
    void 한국어_영어_특수문자_순으로_아티스트를_정렬한다() {
        // given
        artistCommandRepository.save(ArtistFixture.create("실리카겔"));
        artistCommandRepository.save(ArtistFixture.create("-"));
        artistCommandRepository.save(ArtistFixture.create("검정치마"));
        artistCommandRepository.save(ArtistFixture.create("!"));
        artistCommandRepository.save(ArtistFixture.create("Pia"));
        artistCommandRepository.save(ArtistFixture.create("Stone Temple Pilots"));

        // when
        OffsetBasePaginatedResponse<ArtistGetResponse> result = sut.getArtistList(PageRequest.ofSize(10));

        // then
        assertAll(
                () -> assertThat(result.data().get(0).name()).isEqualTo("검정치마"),
                () -> assertThat(result.data().get(1).name()).isEqualTo("실리카겔"),
                () -> assertThat(result.data().get(2).name()).isEqualTo("Pia"),
                () -> assertThat(result.data().get(3).name()).isEqualTo("Stone Temple Pilots"),
                () -> assertThat(result.data().get(4).name()).isEqualTo("!"),
                () -> assertThat(result.data().get(5).name()).isEqualTo("-")
        );
    }

    @Test
    void ti가_포함된_아티스트를_검색한다() {
        // given
        String query = "ti";

        List<Artist> artistList = new ArrayList<>();
        for (long i = 0; i < 10; i++) {
            Artist artist = ArtistFixture.create(i, "artist" + i);
            artistList.add(artistCommandRepository.save(artist));
        }

        // when
        OffsetBasePaginatedResponse<ArtistGetResponse> response = sut.searchArtists(query, PageRequest.of(0, 3));

        // then
        assertSoftly(s -> {
                    s.assertThat(response.hasNext()).isTrue();
                    s.assertThat(response.data().size()).isEqualTo(3);
                    s.assertThat(response.data().get(0).id()).isEqualTo(1);
                    s.assertThat(response.data().get(0).name()).isEqualTo("artist1");
                    s.assertThat(response.data().get(1).id()).isEqualTo(2);
                    s.assertThat(response.data().get(1).name()).isEqualTo("artist2");
                    s.assertThat(response.data().get(2).id()).isEqualTo(3);
                    s.assertThat(response.data().get(2).name()).isEqualTo("artist3");
                }
        );
    }
}
