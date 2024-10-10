package com.projectlyrics.server.domain.artist.service;

import com.projectlyrics.server.support.IntegrationTest;
import com.projectlyrics.server.support.fixture.ArtistFixture;
import com.projectlyrics.server.domain.artist.dto.response.ArtistGetResponse;
import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.artist.repository.ArtistCommandRepository;
import com.projectlyrics.server.domain.artist.repository.ArtistQueryRepository;
import com.projectlyrics.server.domain.common.dto.util.CursorBasePaginatedResponse;
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
    void 아티스트의_데이터를_커서_기반_페이지네이션으로_조회한다() throws Exception {
        //given
        Artist artist1 = artistCommandRepository.save(ArtistFixture.create("실리카겔"));
        Artist artist2 = artistCommandRepository.save(ArtistFixture.create("잔나비"));
        Artist artist3 = artistCommandRepository.save(ArtistFixture.create("너드커넥션"));
        List<Artist> artistList = List.of(artist1, artist2, artist3);

        long cursor = 0L;
        Pageable pageable = PageRequest.of(0, 3);

        //when
        CursorBasePaginatedResponse<ArtistGetResponse> response = sut.getArtistList(cursor, pageable);

        //then
        assertSoftly(s -> {
                    s.assertThat(response.hasNext()).isFalse();
                    s.assertThat(response.data().size()).isEqualTo(artistList.size());
                    for (int i = 0; i < artistList.size(); i++) {
                        s.assertThat(response.data().get(i).id()).isEqualTo(artistList.get(i).getId());
                        s.assertThat(response.data().get(i).name()).isEqualTo(artistList.get(i).getName());
                    }
                }
        );
    }

    @Test
    void 검색어_기반으로_아티스트의_데이터를_커서_기반_페이지네이션으로_조회한다() throws Exception {
        //given
        Artist artist1 = artistCommandRepository.save(ArtistFixture.create("실리카겔"));
        artistCommandRepository.save(ArtistFixture.create("잔나비"));
        artistCommandRepository.save(ArtistFixture.create("너드커넥션"));

        long cursor = 0L;
        Pageable pageable = PageRequest.of(0, 3);

        //when
        CursorBasePaginatedResponse<ArtistGetResponse> response = sut.searchArtists("실리", cursor, pageable);

        //then
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
        CursorBasePaginatedResponse<ArtistGetResponse> result = sut.getArtistList(null, PageRequest.ofSize(10));

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
        long cursor = 5L;
        Pageable pageable = PageRequest.of(0, 3);
        ArrayList<Artist> artistList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Artist artist = ArtistFixture.create((long) i, "artist" + i);
            artistCommandRepository.save(artist);
            artistList.add(artist);
        }

        // when
        CursorBasePaginatedResponse<ArtistGetResponse> response = sut.searchArtists(query, cursor, pageable);

        // then
        assertSoftly(s -> {
                    s.assertThat(response.hasNext()).isTrue();
                    s.assertThat(response.nextCursor()).isEqualTo(8);
                    s.assertThat(response.data().size()).isEqualTo(3);
                    s.assertThat(response.data().get(0).id()).isEqualTo(6);
                    s.assertThat(response.data().get(0).name()).isEqualTo("artist6");
                    s.assertThat(response.data().get(1).id()).isEqualTo(7);
                    s.assertThat(response.data().get(1).name()).isEqualTo("artist7");
                    s.assertThat(response.data().get(2).id()).isEqualTo(8);
                    s.assertThat(response.data().get(2).name()).isEqualTo("artist8");
                }
        );
    }
}
