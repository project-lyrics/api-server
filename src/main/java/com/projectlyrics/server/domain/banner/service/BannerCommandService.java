package com.projectlyrics.server.domain.banner.service;

import com.projectlyrics.server.domain.banner.domain.Banner;
import com.projectlyrics.server.domain.banner.domain.BannerCreate;
import com.projectlyrics.server.domain.banner.dto.request.BannerCreateRequest;
import com.projectlyrics.server.domain.banner.repository.BannerCommandRepository;
import com.projectlyrics.server.domain.banner.repository.BannerQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class BannerCommandService {

    private final BannerQueryRepository bannerQueryRepository;
    private final BannerCommandRepository bannerCommandRepository;

    @Value("${default_banner_id}")
    private Long defaultBannerId;

    public Banner create(BannerCreateRequest request) {
        if (request.hideOther()) {
            Long hiddenBannerId = request.hiddenBannerId() == null ? defaultBannerId : request.hiddenBannerId();
            Banner hiddenBanner =  bannerQueryRepository.findById(hiddenBannerId);
            hiddenBanner.setStartDate(request.dueDate() == null ? null : request.dueDate().atStartOfDay());
        }
        return bannerCommandRepository.save(Banner.create(BannerCreate.of(request)));
    }
}
