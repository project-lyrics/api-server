package com.projectlyrics.server.domain.artist.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.projectlyrics.server.support.RepositoryTest;
import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.support.fixture.ArtistFixture;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
        Artist artist1 = artistCommandRepository.save(ArtistFixture.create());
        Artist artist2 = artistCommandRepository.save(ArtistFixture.create());
        Artist artist3 = artistCommandRepository.save(ArtistFixture.create());
        List<Artist> artistList = List.of(artist1, artist2, artist3);

        long cursor = 0L;
        Pageable pageable = PageRequest.of(0, 3);

        // when
        Slice<Artist> artistSlice = artistQueryRepository.findAll(pageable);

        // then
        assertAll(
                () -> assertThat(artistSlice.getNumber()).isEqualTo(cursor),
                () -> assertThat(artistSlice.getSize()).isEqualTo(artistList.size()),
                () -> assertThat(artistSlice.getNumberOfElements()).isEqualTo(pageable.getPageSize()),
                () -> assertThat(artistSlice.getContent()).isEqualTo(artistList)
        );
    }

    @Test
    void 아티스트_리스트를_조회하는_페이지로_인한_오프셋이_데이터를_넘을_경우_Slice의_content가_비어있다() throws Exception {
        // given
        Artist artist1 = artistCommandRepository.save(ArtistFixture.create());
        Pageable pageable = PageRequest.of(2, 100);

        // when
        Slice<Artist> artistSlice = artistQueryRepository.findAll(pageable);

        // then
        assertAll(
                () -> assertThat(artistSlice.getSize()).isEqualTo(pageable.getPageSize()),
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
        Slice<Artist> searchedArtists = artistQueryRepository.findAllByQuery("검정치마", PageRequest.of(0, 3));

        // then
        assertThat(searchedArtists.getContent().getFirst().getId()).isEqualTo(artist1.getId());
    }

    @Test
    void 사용자_입력_쿼리가_아무런_아티스트를_검색하지_못하면_Slice의_content가_비어있다() throws Exception {
        // given
        Artist artist1 = ArtistFixture.create("검정치마");
        artistCommandRepository.save(artist1);

        // when
        Slice<Artist> searchedArtists = artistQueryRepository.findAllByQuery("긴난보이즈", PageRequest.of(0, 3));

        // then
        assertThat(searchedArtists.getContent()).isEmpty();
    }

}
