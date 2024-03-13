package com.projectlyrics.server.domain.artist.service;

import com.projectlyrics.server.domain.artist.dto.request.AddArtistRequest;
import com.projectlyrics.server.domain.artist.dto.request.UpdateArtistRequest;
import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.artist.repository.ArtistRepository;
import com.projectlyrics.server.domain.artist.service.impl.ArtistServiceImpl;
import com.projectlyrics.server.global.exception.BusinessException;
import com.projectlyrics.server.utils.ArtistTestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ArtistServiceTest {

  @InjectMocks
  private ArtistServiceImpl sut;

  @Mock
  private ArtistRepository artistRepository;

  @Captor
  private ArgumentCaptor<Artist> addArtistArgumentCaptor;

  @Test
  void 전달받은_데이터로_새로운_아티스트를_추가한다() {
    // given
    var artist = ArtistTestUtil.create();
    var addArtistRequest = createAddArtistRequest();
    given(artistRepository.save(any(Artist.class))).willReturn(artist);

    // when
    sut.addArtist(addArtistRequest);

    // then
    then(artistRepository).should().save(addArtistArgumentCaptor.capture());
    var captorValue = addArtistArgumentCaptor.getValue();
    assertThat(addArtistRequest.name()).isEqualTo(captorValue.getName());
    assertThat(addArtistRequest.englishName()).isEqualTo(captorValue.getEnglishName());
    assertThat(addArtistRequest.profileImageCdnLink()).isEqualTo(captorValue.getProfileImageCdnLink());
  }

  @Test
  void NULL_또는_공백_빈문자열이_아닌_데이터로만_아티스트의_데이터를_수정한다() {
    // given
    Long artistId = 1L;
    var artist = ArtistTestUtil.create();
    var updateArtistRequest = createUpdateArtistRequest("   ", null, "https://~2");
    given(artistRepository.findByIdAndNotDeleted(artistId)).willReturn(Optional.of(artist));

    // when
    var updateArtistResponse = sut.updateArtist(artistId, updateArtistRequest);

    // then
    then(artistRepository).should().findByIdAndNotDeleted(anyLong());
    assertThat(updateArtistResponse.name()).isEqualTo(artist.getName());
    assertThat(updateArtistResponse.englishName()).isEqualTo(artist.getEnglishName());
    assertThat(updateArtistResponse.profileImageCdnLink()).isEqualTo(updateArtistRequest.profileImageCdnLink());
  }

  @Test
  void 데이터_수정_시_profileImageCdnLink가_https로_시작하지_않는다면_에러가_발생한다() {
    // given
    Long artistId = 1L;
    var artist = ArtistTestUtil.create();
    var updateArtistRequest = createUpdateArtistRequest(null, null, "http://~2");
    given(artistRepository.findByIdAndNotDeleted(artistId)).willReturn(Optional.of(artist));

    // when
    Throwable throwable = catchThrowable(() -> sut.updateArtist(artistId, updateArtistRequest));

    // then
    then(artistRepository).should().findByIdAndNotDeleted(anyLong());
    assertThat(throwable).isInstanceOf(BusinessException.class);
  }

  private AddArtistRequest createAddArtistRequest() {
    return new AddArtistRequest("넬", "NELL", "https://~");
  }

  private UpdateArtistRequest createUpdateArtistRequest(String name, String englishName, String profileImageCdnLink) {
    return new UpdateArtistRequest(name, englishName, profileImageCdnLink);
  }
}
