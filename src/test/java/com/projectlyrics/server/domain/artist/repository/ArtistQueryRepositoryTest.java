package com.projectlyrics.server.domain.artist.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.projectlyrics.server.common.RepositoryTest;
import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.common.fixture.ArtistFixture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.List;

@RepositoryTest
public class ArtistQueryRepositoryTest {

  @Autowired
  private ArtistQueryRepository artistQueryRepository;

  @Autowired
  private ArtistCommandRepository artistCommandRepository;

  @Test
  void 아티스트의_데이터를_사용자_입력_쿼리_기반으로_조회해_반환한다() {
    // given
    Artist artist1 = ArtistFixture.createWithName("검정치마");
    Artist artist2 = ArtistFixture.createWithName("구남과여라이딩스텔라");
    artistCommandRepository.save(artist1);
    artistCommandRepository.save(artist2);

    // when
    Slice<Artist> searchedArtists = artistQueryRepository.findAllByQueryAndNotDeleted("검정치마", 0L,
        PageRequest.of(0, 3));

    // then
    assertThat(searchedArtists.getContent().getFirst().getId()).isEqualTo(artist1.getId());
  }

  @Test
  void 아티스트를_페이징으로_조회한다() throws Exception {
    //given
    Artist artist1 = artistCommandRepository.save(ArtistFixture.createWithName("실리카겔"));
    Artist artist2 = artistCommandRepository.save(ArtistFixture.createWithName("잔나비"));
    Artist artist3 = artistCommandRepository.save(ArtistFixture.createWithName("너드커넥션"));
    List<Artist> artistList = List.of(artist1, artist2, artist3);

    long cursor = 0L;
    Pageable pageable = PageRequest.of(0, 3);

    //when
    Slice<Artist> artistSlice = artistQueryRepository.findAllAndNotDeleted(cursor, pageable);

    //then
    assertAll(
        () -> assertThat(artistSlice.getNumber()).isEqualTo(cursor),
        () -> assertThat(artistSlice.getSize()).isEqualTo(artistList.size()),
        () -> assertThat(artistSlice.getNumberOfElements()).isEqualTo(pageable.getPageSize()),
        () -> assertThat(artistSlice.getContent()).isEqualTo(artistList)
    );
  }
}
