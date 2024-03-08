package com.projectlyrics.server.domain.artist.service;

import com.projectlyrics.server.domain.artist.dto.AddArtistRequest;
import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.artist.repository.ArtistRepository;
import com.projectlyrics.server.domain.artist.service.impl.ArtistServiceImpl;
import com.projectlyrics.server.utils.ArtistTestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

@ExtendWith(MockitoExtension.class)
class ArtistServiceTest {

  @InjectMocks
  private ArtistServiceImpl sut;

  @Mock
  private ArtistRepository artistRepository;

  @Test
  void 전달받은_데이터로_새로운_아티스트를_추가한다() {
    // given
    Long artistId = 1L;
    Artist artist = spy(ArtistTestUtil.create());
    AddArtistRequest addArtistRequest = createAddArtistRequest();
    given(artistRepository.save(any(Artist.class))).willReturn(artist);
    doReturn(artistId).when(artist).getId();

    // when
    Long actualResult = sut.addArtist(addArtistRequest);

    // then
    then(artistRepository).should().save(any(Artist.class));
    assertThat(actualResult).isEqualTo(artistId);
  }

  private AddArtistRequest createAddArtistRequest() {
    return new AddArtistRequest("넬", "NELL", "https://~");
  }
}
