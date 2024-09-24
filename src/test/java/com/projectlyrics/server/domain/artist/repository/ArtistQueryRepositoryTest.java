package com.projectlyrics.server.domain.artist.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.projectlyrics.server.domain.artist.dto.response.ArtistGetResponse;
import com.projectlyrics.server.support.RepositoryTest;
import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.support.fixture.ArtistFixture;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import java.util.List;

@RepositoryTest
public class ArtistQueryRepositoryTest {

    @Autowired
    private ArtistQueryRepository artistQueryRepository;

    @Autowired
    private ArtistCommandRepository artistCommandRepository;

    @Test
    void id로_Optional로_감싼_아티스트를_조회한다() {
        // given
        Artist savedArtist = artistCommandRepository.save(ArtistFixture.create());

        // when
        Optional<Artist> artist = artistQueryRepository.findById(savedArtist.getId());

        // then
        assertAll(
                () -> assertThat(artist).isPresent(),
                () -> assertThat(artist.get().getId()).isEqualTo(savedArtist.getId()),
                () -> assertThat(artist.get().getDeletedAt()).isNull()
        );
    }

    @Test
    void 존재하지_않는_id로_아티스트를_조회하면_Optional_empty가_반환된다() {
        // given, when
        Optional<Artist> artist = artistQueryRepository.findById(1L);

        // then
        assertThat(artist).isEmpty();
    }

    @Test
    void 커서_기반_페이징으로_아티스트_리스트를_조회한다() throws Exception {
        // given
        List<ArtistGetResponse> response = List.of(
                ArtistGetResponse.of(artistCommandRepository.save(ArtistFixture.create())),
                ArtistGetResponse.of(artistCommandRepository.save(ArtistFixture.create())),
                ArtistGetResponse.of(artistCommandRepository.save(ArtistFixture.create()))
        );

        long cursor = 0L;
        int size = 3;

        // when
        Slice<ArtistGetResponse> artistSlice = artistQueryRepository.findAll(cursor, size);

        // then
        assertAll(
                () -> assertThat(artistSlice.getNumber()).isEqualTo(cursor),
                () -> assertThat(artistSlice.getSize()).isEqualTo(response.size()),
                () -> assertThat(artistSlice.getNumberOfElements()).isEqualTo(size),
                () -> assertThat(artistSlice.getContent()).isEqualTo(response)
        );
    }

    @Test
    void 아티스트_리스트를_조회하는_커서가_최대_id값보다_클_경우_Slice의_content가_비어있다() throws Exception {
        // given
        artistCommandRepository.save(ArtistFixture.create());

        long cursor = 1000L;
        int size = 3;

        // when
        Slice<ArtistGetResponse> artistSlice = artistQueryRepository.findAll(cursor, size);

        // then
        assertAll(
                () -> assertThat(artistSlice.getSize()).isEqualTo(size),
                () -> assertThat(artistSlice.getNumberOfElements()).isEqualTo(0),
                () -> assertThat(artistSlice.getContent()).isEmpty()
        );
    }

    @Test
    void 아티스트의_데이터를_사용자_입력_쿼리_기반으로_조회해_반환한다() {
        // given
        Artist artist1 = ArtistFixture.create("검정치마");
        Artist artist2 = ArtistFixture.create("구남과여라이딩스텔라");
        artistCommandRepository.save(artist1);
        artistCommandRepository.save(artist2);

        // when
        Slice<Artist> searchedArtists = artistQueryRepository.findAllByQuery("검정치마", 0L, PageRequest.of(0, 3));

        // then
        assertThat(searchedArtists.getContent().getFirst().getId()).isEqualTo(artist1.getId());
    }

    @Test
    void 사용자_입력_쿼리가_아무런_아티스트를_검색하지_못하면_Slice의_content가_비어있다() throws Exception {
        // given
        Artist artist1 = ArtistFixture.create("검정치마");
        artistCommandRepository.save(artist1);

        // when
        Slice<Artist> searchedArtists = artistQueryRepository.findAllByQuery("긴난보이즈", 0L, PageRequest.of(0, 3));

        // then
        assertThat(searchedArtists.getContent()).isEmpty();
    }

}
