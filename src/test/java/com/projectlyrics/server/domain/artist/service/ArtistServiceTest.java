package com.projectlyrics.server.domain.artist.service;

import com.projectlyrics.server.domain.artist.dto.request.AddArtistRequest;
import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.artist.repository.ArtistRepository;
import com.projectlyrics.server.domain.artist.service.impl.ArtistServiceImpl;
import com.projectlyrics.server.utils.ArtistTestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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

  private AddArtistRequest createAddArtistRequest() {
    return new AddArtistRequest("넬", "NELL", "https://~");
  }
}
