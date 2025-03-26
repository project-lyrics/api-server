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
        BannerCreateRequest request = new BannerCreateRequest("imageUrl", "redirectUrl", LocalDate.now(), LocalDate.now());

        // when
        Banner banner = sut.create(request);

        // then
        Banner result = bannerQueryRepository.findById(banner.getId());
        assertAll(
                () -> assertThat(result.getImageUrl()).isEqualTo(request.imageUrl()),
                () -> assertThat(result.getRedirectUrl()).isEqualTo(request.redirectUrl()),
                () -> assertThat(result.getDueDate()).isEqualTo(request.dueDate().atStartOfDay())
        );
    }

    @Test
    void 시작일자_없이도_배너를_발행해야_한다() {
        // given
        BannerCreateRequest request = new BannerCreateRequest("imageUrl", "redirectUrl", null, LocalDate.now());

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
        BannerCreateRequest request = new BannerCreateRequest("imageUrl", "redirectUrl", LocalDate.now(), null);

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
