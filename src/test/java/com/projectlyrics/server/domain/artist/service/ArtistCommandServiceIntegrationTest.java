package com.projectlyrics.server.domain.artist.service;

import com.projectlyrics.server.support.IntegrationTest;
import com.projectlyrics.server.support.fixture.ArtistFixture;
import com.projectlyrics.server.domain.artist.dto.request.ArtistAddRequest;
import com.projectlyrics.server.domain.artist.dto.request.ArtistUpdateRequest;
import com.projectlyrics.server.domain.artist.dto.response.ArtistAddResponse;
import com.projectlyrics.server.domain.artist.dto.response.ArtistUpdateResponse;
import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.artist.exception.ArtistNotFoundException;
import com.projectlyrics.server.domain.artist.repository.ArtistCommandRepository;
import com.projectlyrics.server.domain.artist.repository.ArtistQueryRepository;
import com.projectlyrics.server.domain.common.message.ErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

class ArtistCommandServiceIntegrationTest extends IntegrationTest {

    @Autowired
    ArtistCommandService sut;

    @Autowired
    ArtistCommandRepository artistCommandRepository;

    @Autowired
    ArtistQueryRepository artistQueryRepository;

    @Test
    void 새로운_아티스트를_추가한다() throws Exception {
        //given
        ArtistAddRequest request = createAddArtistRequest();

        //when
        ArtistAddResponse response = sut.addArtist(request);

        //then
        Artist artist = artistQueryRepository.findByIdAndNotDeleted(response.id()).get();
        assertAll(
                () -> assertThat(artist.getName()).isEqualTo(request.name()),
                () -> assertThat(artist.getImageUrl()).isEqualTo(request.imageUrl()),
                () -> assertThat(artist.isInUse()).isTrue()
        );
    }

    @Test
    void 아티스트를_수정한다() throws Exception {
        //given
        Artist savedArtist = artistCommandRepository.save(ArtistFixture.create());
        ArtistUpdateRequest request = createUpdateArtistRequest("너드커넥션", "https://~2");

        //when
        ArtistUpdateResponse response = sut.updateArtist(savedArtist.getId(), request);

        //then
        Artist artist = artistQueryRepository.findByIdAndNotDeleted(response.id()).get();
        assertAll(
                () -> assertThat(artist.getName()).isEqualTo(request.name()),
                () -> assertThat(artist.getImageUrl()).isEqualTo(request.profileImageCdnLink())
        );
    }

    @Test
    void 없는_아티스트를_수정할_경우_예외가_발생해야_한다() throws Exception {
        //given
        ArtistUpdateRequest request = createUpdateArtistRequest("너드커넥션", "https://~2");

        //when
        Throwable throwable = catchThrowable(() -> sut.updateArtist(1L, request));

        //then
        assertThat(throwable).isInstanceOf(ArtistNotFoundException.class)
                .hasMessage(ErrorCode.ARTIST_NOT_FOUND.getErrorMessage());
    }

    private ArtistAddRequest createAddArtistRequest() {
        return new ArtistAddRequest("넬", "https://~");
    }

    private ArtistUpdateRequest createUpdateArtistRequest(String name, String profileImageCdnLink) {
        return new ArtistUpdateRequest(name, profileImageCdnLink);
    }
}
