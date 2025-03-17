package com.projectlyrics.server.domain.banner.service;

import com.projectlyrics.server.domain.banner.domain.Banner;
import com.projectlyrics.server.domain.banner.domain.BannerCreate;
import com.projectlyrics.server.domain.banner.dto.request.BannerCreateRequest;
import com.projectlyrics.server.domain.banner.repository.BannerCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class BannerCommandService {

    private final BannerCommandRepository bannerCommandRepository;

    public Banner create(BannerCreateRequest request) {
        return bannerCommandRepository.save(Banner.create(BannerCreate.of(request)));
    }
}
