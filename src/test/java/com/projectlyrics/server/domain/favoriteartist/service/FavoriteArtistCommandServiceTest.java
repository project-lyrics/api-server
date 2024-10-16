package com.projectlyrics.server.domain.favoriteartist.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.artist.repository.ArtistCommandRepository;
import com.projectlyrics.server.domain.common.entity.enumerate.EntityStatusEnum;
import com.projectlyrics.server.domain.favoriteartist.dto.request.CreateFavoriteArtistListRequest;
import com.projectlyrics.server.domain.favoriteartist.entity.FavoriteArtist;
import com.projectlyrics.server.domain.favoriteartist.repository.FavoriteArtistQueryRepository;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.exception.UserNotFoundException;
import com.projectlyrics.server.domain.user.repository.UserCommandRepository;
import com.projectlyrics.server.domain.user.repository.UserQueryRepository;
import com.projectlyrics.server.support.IntegrationTest;
import com.projectlyrics.server.support.fixture.ArtistFixture;
import com.projectlyrics.server.support.fixture.UserFixture;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

class FavoriteArtistCommandServiceTest extends IntegrationTest {

    @Autowired
    UserCommandRepository userCommandRepository;


    @Autowired
    UserQueryRepository userQueryRepository;

    @Autowired
    FavoriteArtistCommandService sut;

    @Autowired
    ArtistCommandRepository artistCommandRepository;

    @Autowired
    FavoriteArtistQueryRepository favoriteArtistQueryRepository;

    @Test
    void 관심_아티스트를_저장해야_한다() throws Exception {
        // given
        Artist artist = artistCommandRepository.save(ArtistFixture.create());
        User user = userCommandRepository.save(UserFixture.create());

        // when
        sut.create(user.getId(), artist.getId());

        // then
        User newUser = userQueryRepository.findById(user.getId()).orElseThrow(UserNotFoundException::new);
        List<FavoriteArtist> result = favoriteArtistQueryRepository.findAllByUserId(user.getId(), null, PageRequest.ofSize(5)).getContent();

        assertAll(
                () -> assertThat(result).hasSize(1),
                () -> assertThat(result.getFirst().getUser()).isEqualTo(user),
                () -> assertThat(result.getFirst().getArtist()).isEqualTo(artist),
                () -> assertThat(newUser.getStatus()).isEqualTo(EntityStatusEnum.IN_USE)
        );
    }

    @Test
    void 관심_아티스트_리스트를_저장해야_한다() throws Exception {
        //given
        Artist artist1 = artistCommandRepository.save(ArtistFixture.create());
        Artist artist2 = artistCommandRepository.save(ArtistFixture.create());
        Artist artist3 = artistCommandRepository.save(ArtistFixture.create());
        CreateFavoriteArtistListRequest request = new CreateFavoriteArtistListRequest(List.of(
                        artist1.getId(),
                        artist2.getId(),
                        artist3.getId()
                )
        );
        User user = userCommandRepository.save(UserFixture.create());

        //when
        sut.createAll(user.getId(), request);

        //then
        User newUser = userQueryRepository.findById(user.getId()).orElseThrow(UserNotFoundException::new);
        PageRequest pageRequest = PageRequest.ofSize(5);
        Slice<FavoriteArtist> favoriteArtistSlice = favoriteArtistQueryRepository.findAllByUserId(user.getId(), null, pageRequest);
        List<Artist> artistList = List.of(artist1, artist2, artist3);
        assertSoftly(s -> {
            s.assertThat(favoriteArtistSlice.getContent()).hasSize(3);
            s.assertThat(favoriteArtistSlice.hasNext()).isFalse();
            favoriteArtistSlice.getContent().forEach(favoriteArtist -> {
                s.assertThat(favoriteArtist.getUser()).isEqualTo(user);
                s.assertThat(artistList.contains(favoriteArtist.getArtist())).isTrue();
            });
            assertThat(newUser.getStatus()).isEqualTo(EntityStatusEnum.IN_USE);
        });
    }

    @Test
    void 관심_아티스트를_soft_delete_해야_한다() throws Exception {
        // given
        Artist artist = artistCommandRepository.save(ArtistFixture.create());
        User user = userCommandRepository.save(UserFixture.create());
        CreateFavoriteArtistListRequest request = new CreateFavoriteArtistListRequest(List.of(artist.getId()));
        sut.createAll(user.getId(), request);

        // when
        sut.delete(user.getId(), artist.getId());

        // then
        assertThat(favoriteArtistQueryRepository.findByUserIdAndArtistId(user.getId(), artist.getId())).isEmpty();
    }
}
