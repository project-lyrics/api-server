package com.projectlyrics.server.domain.banner.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.projectlyrics.server.domain.banner.domain.Banner;
import com.projectlyrics.server.domain.banner.domain.BannerCreate;
import com.projectlyrics.server.domain.banner.dto.request.BannerCreateRequest;
import com.projectlyrics.server.domain.banner.dto.response.BannerGetResponse;
import com.projectlyrics.server.domain.banner.repository.BannerCommandRepository;
import com.projectlyrics.server.domain.banner.repository.BannerQueryRepository;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.repository.UserCommandRepository;
import com.projectlyrics.server.support.IntegrationTest;
import com.projectlyrics.server.support.fixture.UserFixture;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class BannerQueryServiceTest extends IntegrationTest {

    @Autowired
    UserCommandRepository userCommandRepository;

    @Autowired
    BannerQueryRepository bannerQueryRepository;

    @Autowired
    BannerCommandRepository bannerCommandRepository;

    @Autowired
    BannerQueryService sut;

    private User user;

    private BannerCreateRequest activeBannerCreateRequest;
    private BannerCreateRequest expiredBannerCreateRequest;
    private BannerCreateRequest bannerCreateRequest;

    @BeforeEach
    void setUp() {
        user = userCommandRepository.save(UserFixture.create());
        activeBannerCreateRequest = new BannerCreateRequest(
                "imageUrl",
                "redirectUrl",
                LocalDate.now().plusDays(1)
        );
        expiredBannerCreateRequest = new BannerCreateRequest(
                "imageUrl",
                "redirectUrl",
                LocalDate.now().minusDays(1)
        );
        bannerCreateRequest = new BannerCreateRequest(
                "imageUrl",
                "redirectUrl",
                null
        );
    }

    @Test
    void 배너를_특정_개수만큼_최신순으로_조회해야_한다() {
        // given
        List<Banner> banners = new ArrayList<>();
        for (int i=0; i<5; i++) {
            banners.add(bannerCommandRepository.save(Banner.create(BannerCreate.of(activeBannerCreateRequest))));
        }

        // when
        List<BannerGetResponse> result = sut.getRecentBanners(5);

        // then
        assertAll(
                () -> assertThat(result.size()).isEqualTo(5),
                () -> assertThat(result.get(0).id()).isEqualTo(banners.get(4).getId()),
                () -> assertThat(result.get(1).id()).isEqualTo(banners.get(3).getId()),
                () -> assertThat(result.get(2).id()).isEqualTo(banners.get(2).getId()),
                () -> assertThat(result.get(3).id()).isEqualTo(banners.get(1).getId()),
                () -> assertThat(result.get(4).id()).isEqualTo(banners.get(0).getId())
        );
    }

    @Test
    void 배너_리스트_조회시_주어진_개수만큼의_배너가_없으면_최대한_많은_배너를_조회해야_한다() {
        // given
        List<Banner> banners = new ArrayList<>();
        for (int i=0; i<5; i++) {
            banners.add(bannerCommandRepository.save(Banner.create(BannerCreate.of(activeBannerCreateRequest))));
        }

        // when
        List<BannerGetResponse> result = sut.getRecentBanners(10);

        // then
        assertAll(
                () -> assertThat(result.size()).isEqualTo(5),
                () -> assertThat(result.get(0).id()).isEqualTo(banners.get(4).getId()),
                () -> assertThat(result.get(1).id()).isEqualTo(banners.get(3).getId()),
                () -> assertThat(result.get(2).id()).isEqualTo(banners.get(2).getId()),
                () -> assertThat(result.get(3).id()).isEqualTo(banners.get(1).getId()),
                () -> assertThat(result.get(4).id()).isEqualTo(banners.get(0).getId())
        );
    }

    @Test
    void 배너_리스트_조회시_마감기한이_이미_지난_배너는_제외해야_한다() {
        // given
        List<Banner> banners = new ArrayList<>();
        for (int i=0; i<3; i++) {
            banners.add(bannerCommandRepository.save(Banner.create(BannerCreate.of(activeBannerCreateRequest))));
        }
        for (int i=0; i<2; i++) {
            banners.add(bannerCommandRepository.save(Banner.create(BannerCreate.of(bannerCreateRequest))));
        }
        for (int i=0; i<5; i++) {
            banners.add(bannerCommandRepository.save(Banner.create(BannerCreate.of(expiredBannerCreateRequest))));
        }

        // when
        List<BannerGetResponse> result = sut.getRecentBanners(10);

        // then
        assertAll(
                () -> assertThat(result.size()).isEqualTo(5),
                () -> assertThat(result.get(0).id()).isEqualTo(banners.get(4).getId()),
                () -> assertThat(result.get(1).id()).isEqualTo(banners.get(3).getId()),
                () -> assertThat(result.get(2).id()).isEqualTo(banners.get(2).getId()),
                () -> assertThat(result.get(3).id()).isEqualTo(banners.get(1).getId()),
                () -> assertThat(result.get(4).id()).isEqualTo(banners.get(0).getId())
        );
    }
}
