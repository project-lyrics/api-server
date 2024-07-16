package com.projectlyrics.server.domain.artist.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import com.projectlyrics.server.domain.artist.dto.response.ArtistGetResponse;
import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.artist.repository.ArtistQueryRepository;
import com.projectlyrics.server.domain.common.dto.util.CursorBasePaginatedResponse;
import com.projectlyrics.server.support.fixture.ArtistFixture;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;

@ExtendWith(MockitoExtension.class)
class ArtistQueryServiceTest {

    @InjectMocks
    private ArtistQueryService sut;

    @Mock
    private ArtistQueryRepository artistQueryRepository;

    @Test
    void 아티스트의_PK를_전달받아_아티스트_데이터를_조회해_반환한다() {
        // given
        Long artistId = 1L;
        Artist artist = Mockito.spy(ArtistFixture.create());
        given(artistQueryRepository.findByIdAndNotDeleted(artistId)).willReturn(Optional.of(artist));
        doReturn(artistId).when(artist).getId();

        // when
        ArtistGetResponse getArtistResponse = ArtistGetResponse.of(sut.getArtistById(artistId));

        // then
        then(artistQueryRepository).should().findByIdAndNotDeleted(anyLong());
        assertThat(getArtistResponse.id()).isEqualTo(artistId);
        assertThat(getArtistResponse.name()).isEqualTo(artist.getName());
        assertThat(getArtistResponse.imageUrl()).isEqualTo(artist.getImageUrl());
    }

    @Test
    void 아티스트의_데이터를_커서_기반_페이지네이션으로_조회해_반환한다() {
        // given
        long cursor = 5L;
        Pageable pageable = PageRequest.of(0, 3);
        Artist artist = spy(ArtistFixture.createWithName("실리카겔"));
        Artist artist2 = spy(ArtistFixture.createWithName("잔나비"));
        Artist artist3 = spy(ArtistFixture.createWithName("너드커넥션"));
        List<Artist> artistList = List.of(artist, artist2, artist3);
        given(artistQueryRepository.findAllAndNotDeleted(cursor, pageable))
                .willReturn(new SliceImpl<>(artistList, pageable, true));
        doReturn(5L).when(artist).getId();
        doReturn(6L).when(artist2).getId();
        doReturn(7L).when(artist3).getId();

        // when
        CursorBasePaginatedResponse<ArtistGetResponse> artistListResponse = sut.getArtistList(cursor, pageable);

        // then
        then(artistQueryRepository).should().findAllAndNotDeleted(cursor, pageable);
        assertThat(artistListResponse.data().size()).isEqualTo(artistList.size());
        assertThat(artistListResponse.hasNext()).isTrue();
        for (int i = 0; i < artistList.size(); i++) {
            assertThat(artistListResponse.data().get(i).name()).isEqualTo(artistList.get(i).getName());
        }
    }

    @Test
    void 비어_있는_아티스트_리스트에_대해_null_예외가_발생하지_않는다() {
        // given
        long cursor = 0L;
        Pageable pageable = PageRequest.of(0, 3);
        given(artistQueryRepository.findAllAndNotDeleted(cursor, pageable))
                .willReturn(new SliceImpl<>(List.of(), pageable, true));

        // when
        CursorBasePaginatedResponse<ArtistGetResponse> artistsResponse = sut.getArtistList(cursor, pageable);

        // then
        assertThat(artistsResponse).isNotNull();
    }
}