package com.projectlyrics.server.domain.artist.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.projectlyrics.server.domain.artist.dto.request.ArtistAddRequest;
import com.projectlyrics.server.domain.artist.dto.request.ArtistUpdateRequest;
import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.artist.repository.ArtistCommandRepository;
import com.projectlyrics.server.domain.artist.repository.ArtistQueryRepository;
import com.projectlyrics.server.domain.common.entity.enumerate.EntityStatusEnum;
import com.projectlyrics.server.global.exception.FeelinException;
import com.projectlyrics.server.utils.ArtistTestUtil;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ArtistCommandServiceTest {

  @InjectMocks
  private ArtistCommandService sut;

  @Mock
  private ArtistCommandRepository artistCommandRepository;

  @Mock
  private ArtistQueryRepository artistQueryRepository;

  @Captor
  private ArgumentCaptor<Artist> addArtistArgumentCaptor;

  @Test
  void 전달받은_데이터로_새로운_아티스트를_추가한다() {
    // given
    var artist = ArtistTestUtil.create();
    var addArtistRequest = createAddArtistRequest();
    given(artistCommandRepository.save(any(Artist.class))).willReturn(artist);

    // when
    sut.addArtist(addArtistRequest);

    // then
    then(artistCommandRepository).should().save(addArtistArgumentCaptor.capture());
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
    given(artistQueryRepository.findByIdAndNotDeleted(artistId)).willReturn(Optional.of(artist));

    // when
    var updateArtistResponse = sut.updateArtist(artistId, updateArtistRequest);

    // then
    then(artistQueryRepository).should().findByIdAndNotDeleted(anyLong());
    assertThat(updateArtistResponse.name()).isEqualTo(artist.getName());
    assertThat(updateArtistResponse.englishName()).isEqualTo(artist.getEnglishName());
    assertThat(updateArtistResponse.profileImageCdnLink()).isEqualTo(updateArtistRequest.profileImageCdnLink());
  }

  @Test
  void 아티스트_데이터_수정_시_profileImageCdnLink가_https로_시작하지_않는다면_에러가_발생한다() {
    // given
    Long artistId = 1L;
    var artist = ArtistTestUtil.create();
    var updateArtistRequest = createUpdateArtistRequest(null, null, "http://~2");
    given(artistQueryRepository.findByIdAndNotDeleted(artistId)).willReturn(Optional.of(artist));

    // when
    Throwable throwable = catchThrowable(() -> sut.updateArtist(artistId, updateArtistRequest));

    // then
    then(artistQueryRepository).should().findByIdAndNotDeleted(anyLong());
    assertThat(throwable).isInstanceOf(FeelinException.class);
  }

  @Test
  void 아티스트_데이터_삭제_시_soft_delete를_시킨다() {
    // given
    Long artistId = 1L;
    var artist = ArtistTestUtil.create();
    given(artistQueryRepository.findByIdAndNotDeleted(artistId)).willReturn(Optional.of(artist));

    // when
    sut.deleteArtist(artistId);

    // then
    then(artistQueryRepository).should().findByIdAndNotDeleted(anyLong());
    assertThat(artist.getCommonField().getDeletedAt()).isNotNull();
    assertThat(artist.getCommonField().getDeletedBy()).isNotNull();
    assertThat(artist.getCommonField().getStatus()).isEqualTo(EntityStatusEnum.DELETED);
  }

  private ArtistAddRequest createAddArtistRequest() {
    return new ArtistAddRequest("넬", "NELL", "https://~");
  }

  private ArtistUpdateRequest createUpdateArtistRequest(String name, String englishName, String profileImageCdnLink) {
    return new ArtistUpdateRequest(name, englishName, profileImageCdnLink);
  }
}