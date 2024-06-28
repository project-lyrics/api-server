package com.projectlyrics.server.domain.favoriteartist.service;

import com.projectlyrics.server.common.IntegrationTest;
import com.projectlyrics.server.common.fixture.ArtistFixture;
import com.projectlyrics.server.common.fixture.UserFixture;
import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.artist.repository.ArtistCommandRepository;
import com.projectlyrics.server.domain.favoriteartist.dto.request.CreateFavoriteArtistListRequest;
import com.projectlyrics.server.domain.favoriteartist.entity.FavoriteArtist;
import com.projectlyrics.server.domain.favoriteartist.repository.FavoriteArtistQueryRepository;
import com.projectlyrics.server.domain.record.repository.RecordCommandRepository;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.repository.UserCommandRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import java.util.List;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

class FavoriteArtistCommandServiceTest extends IntegrationTest {

    @Autowired
    UserCommandRepository userCommandRepository;

    @Autowired
    FavoriteArtistCommandService sut;

    @Autowired
    ArtistCommandRepository artistCommandRepository;

    @Autowired
    RecordCommandRepository repository;

    @Autowired
    FavoriteArtistQueryRepository favoriteArtistQueryRepository;

    @Test
    void 관심_아티스트_리스트를_저장해야_한다() throws Exception {
        //given
        Artist artist1 = artistCommandRepository.save(ArtistFixture.createWithName("신지훈"));
        Artist artist2 = artistCommandRepository.save(ArtistFixture.createWithName("너드 커넥션"));
        Artist artist3 = artistCommandRepository.save(ArtistFixture.createWithName("한로로"));
        CreateFavoriteArtistListRequest request = new CreateFavoriteArtistListRequest(List.of(
                        artist1.getId(),
                        artist2.getId(),
                        artist3.getId()
                )
        );
        User user = userCommandRepository.save(UserFixture.create());

        //when
        sut.saveAll(user.getId(), request);

        //then
        PageRequest pageRequest = PageRequest.ofSize(5);
        Slice<FavoriteArtist> favoriteArtistSlice = favoriteArtistQueryRepository.findAllByUserId(user.getId(), artist1.getId(), pageRequest);
        List<Artist> artistList = List.of(artist1, artist2, artist3);
        assertSoftly(s -> {
            s.assertThat(favoriteArtistSlice.getContent()).hasSize(3);
            s.assertThat(favoriteArtistSlice.hasNext()).isFalse();
            favoriteArtistSlice.getContent().forEach(favoriteArtist -> {
                s.assertThat(favoriteArtist.getUser()).isEqualTo(user);
                s.assertThat(artistList.contains(favoriteArtist.getArtist())).isTrue();
            });
        });
    }
}
