package com.projectlyrics.server.domain.banner.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.projectlyrics.server.domain.banner.domain.Banner;
import com.projectlyrics.server.domain.banner.dto.request.BannerCreateRequest;
import com.projectlyrics.server.domain.banner.repository.BannerQueryRepository;
import com.projectlyrics.server.domain.user.repository.UserCommandRepository;
import com.projectlyrics.server.support.IntegrationTest;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class BannerCommandServiceTest extends IntegrationTest {

    @Autowired
    UserCommandRepository userCommandRepository;

    @Autowired
    BannerQueryRepository bannerQueryRepository;

    @Autowired
    BannerCommandService sut;

    @Test
    void 배너를_발행해야_한다() {
        // given
        BannerCreateRequest request = new BannerCreateRequest("imageUrl", "redirectUrl", LocalDate.now(), LocalDate.now(), false, null);

        // when
        Banner banner = sut.create(request);

        // then
        Banner result = bannerQueryRepository.findById(banner.getId());
        assertAll(
                () -> assertThat(result.getImageUrl()).isEqualTo(request.imageUrl()),
                () -> assertThat(result.getRedirectUrl()).isEqualTo(request.redirectUrl()),
                () -> assertThat(result.getStartDate()).isEqualTo(request.startDate().atStartOfDay()),
                () -> assertThat(result.getDueDate()).isEqualTo(request.dueDate().atStartOfDay())
        );
    }

    @Test
    void 배너_발행시_필요에_따라_다른_배너의_startDate를_바꿀_수_있어야_한다() {
        // given
        Banner hiddenBanner = sut.create(new BannerCreateRequest("imageUrl", "redirectUrl", LocalDate.now().minusDays(2), LocalDate.now(), false, null));
        BannerCreateRequest request = new BannerCreateRequest("imageUrl", "redirectUrl", LocalDate.now(), LocalDate.now().plusDays(1), true, hiddenBanner.getId());

        // when
        sut.create(request);

        // then
        Banner result = bannerQueryRepository.findById(hiddenBanner.getId());
        assertAll(
                () -> assertThat(result.getStartDate()).isEqualTo(request.dueDate().atStartOfDay())
        );
    }

    @Test
    void 배너_발행시_hiddenBannerId가_주어지지_않으면_기본_배너의_startDate를_변경해야_한다() {
        // given
        // yml에 기본 배너의 id 1로 지정
        sut.create(new BannerCreateRequest("imageUrl", "redirectUrl", LocalDate.now().minusDays(2), LocalDate.now(), false, null));
        BannerCreateRequest request = new BannerCreateRequest("imageUrl", "redirectUrl", LocalDate.now(), LocalDate.now().plusDays(1), true, null);

        // when
        sut.create(request);

        // then
        Banner result = bannerQueryRepository.findById(1L);
        assertAll(
                () -> assertThat(result.getStartDate()).isEqualTo(request.dueDate().atStartOfDay())
        );
    }

    @Test
    void 시작일자_없이도_배너를_발행해야_한다() {
        // given
        BannerCreateRequest request = new BannerCreateRequest("imageUrl", "redirectUrl", null, LocalDate.now(), false, null);

        // when
        Banner banner = sut.create(request);

        // then
        Banner result = bannerQueryRepository.findById(banner.getId());
        assertAll(
                () -> assertThat(result.getImageUrl()).isEqualTo(request.imageUrl()),
                () -> assertThat(result.getRedirectUrl()).isEqualTo(request.redirectUrl()),
                () -> assertThat(result.getStartDate()).isNull(),
                () -> assertThat(result.getDueDate()).isEqualTo(request.dueDate().atStartOfDay())
        );
    }

    @Test
    void 마감일자_없이도_배너를_발행해야_한다() {
        // given
        BannerCreateRequest request = new BannerCreateRequest("imageUrl", "redirectUrl", LocalDate.now(), null, false, null);

        // when
        Banner banner = sut.create(request);

        // then
        Banner result = bannerQueryRepository.findById(banner.getId());
        assertAll(
                () -> assertThat(result.getImageUrl()).isEqualTo(request.imageUrl()),
                () -> assertThat(result.getRedirectUrl()).isEqualTo(request.redirectUrl()),
                () -> assertThat(result.getStartDate()).isEqualTo(request.startDate().atStartOfDay()),
                () -> assertThat(result.getDueDate()).isNull()
        );
    }
}
