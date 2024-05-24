package com.projectlyrics.server.domain.artist.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.utils.ArtistTestUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
public class ArtistQueryRepositoryTest {

  @Autowired
  private QueryArtistRepository queryArtistRepository;

  @Autowired
  private CommandArtistRepository commandArtistRepository;

  @Test
  void 아티스트의_데이터를_사용자_입력_쿼리_기반으로_조회해_반환한다() {
    // given
    var artist1 = ArtistTestUtil.createWithName("검정치마");
    var artist2 = ArtistTestUtil.createWithName("구남과여라이딩스텔라");
    commandArtistRepository.save(artist1);
    commandArtistRepository.save(artist2);

    // when
    var searchedArtists = queryArtistRepository.findAllByQueryAndNotDeleted("검정치마", 0L,
        PageRequest.of(0, 3));

    // then
    assertThat(searchedArtists.getContent().getFirst()).isEqualTo(artist1);
  }
}
