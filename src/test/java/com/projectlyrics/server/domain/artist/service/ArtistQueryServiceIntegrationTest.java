package com.projectlyrics.server.domain.artist.service;

import com.projectlyrics.server.common.IntegrationTest;
import com.projectlyrics.server.common.fixture.ArtistFixture;
import com.projectlyrics.server.domain.artist.dto.response.ArtistGetResponse;
import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.artist.repository.ArtistCommandRepository;
import com.projectlyrics.server.domain.artist.repository.ArtistQueryRepository;
import com.projectlyrics.server.domain.common.dto.util.CursorBasePaginatedResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.*;

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
    Artist artist1 = artistCommandRepository.save(ArtistFixture.createWithName("실리카겔"));
    Artist artist2 = artistCommandRepository.save(ArtistFixture.createWithName("잔나비"));
    Artist artist3 = artistCommandRepository.save(ArtistFixture.createWithName("너드커넥션"));
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
    Artist artist1 = artistCommandRepository.save(ArtistFixture.createWithName("실리카겔"));
    artistCommandRepository.save(ArtistFixture.createWithName("잔나비"));
    artistCommandRepository.save(ArtistFixture.createWithName("너드커넥션"));

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
}
