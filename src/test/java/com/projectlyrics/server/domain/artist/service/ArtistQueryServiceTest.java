package com.projectlyrics.server.domain.artist.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doReturn;

import com.projectlyrics.server.domain.artist.repository.CommandQueryArtistRepository;
import com.projectlyrics.server.domain.artist.service.impl.ArtistQueryServiceImpl;
import com.projectlyrics.server.utils.ArtistTestUtil;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;

@ExtendWith(MockitoExtension.class)
class ArtistQueryServiceTest {

  @InjectMocks
  private ArtistQueryServiceImpl sut;

  @Mock
  private CommandQueryArtistRepository commandArtistRepository;

  @Test
  void 아티스트의_PK를_전달받아_아티스트_데이터를_조회해_반환한다() {
    // given
    Long artistId = 1L;
    var artist = Mockito.spy(ArtistTestUtil.create());
    given(commandArtistRepository.findByIdAndNotDeleted(artistId)).willReturn(Optional.of(artist));
    doReturn(artistId).when(artist).getId();

    // when
    var getArtistResponse = sut.getArtist(artistId);

    // then
    then(commandArtistRepository).should().findByIdAndNotDeleted(anyLong());
    assertThat(getArtistResponse.id()).isEqualTo(artistId);
    assertThat(getArtistResponse.name()).isEqualTo(artist.getName());
    assertThat(getArtistResponse.englishName()).isEqualTo(artist.getEnglishName());
    assertThat(getArtistResponse.profileImageCdnLink()).isEqualTo(artist.getProfileImageCdnLink());
  }

  @Test
  void 아티스트의_데이터를_커서_기반_페이지네이션으로_조회해_반환한다() {
    // given
    var pageable = Pageable.ofSize(15);
    var artistList = List.of(
        ArtistTestUtil.createWithName("실리카겔"),
        ArtistTestUtil.createWithName("잔나비")
    );
    given(commandArtistRepository.findAllAndNotDeleted(pageable)).willReturn(new SliceImpl<>(artistList, pageable, false));

    // when
    var artistListResponse = sut.getArtistList(pageable);

    // then
    then(commandArtistRepository).should().findAllAndNotDeleted(pageable);
    assertThat(artistListResponse.currentCursor()).isEqualTo("0");
    assertThat(artistListResponse.nextCursor()).isEqualTo(null);
    assertThat(artistListResponse.itemSize()).isEqualTo(artistList.size());
    assertThat(artistListResponse.totalSize()).isEqualTo(pageable.getPageSize());
    for (int i = 0; i < artistList.size(); i++) {
      assertThat(artistListResponse.data().get(i).name()).isEqualTo(artistList.get(i).getName());
    }
  }
}