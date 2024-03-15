package com.projectlyrics.server.domain.artist.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doReturn;

import com.projectlyrics.server.domain.artist.repository.CommandQueryArtistRepository;
import com.projectlyrics.server.domain.artist.service.impl.ArtistQueryServiceImpl;
import com.projectlyrics.server.utils.ArtistTestUtil;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

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
}