package com.projectlyrics.server.domain.banner.service;

import com.projectlyrics.server.domain.banner.dto.response.BannerGetResponse;
import com.projectlyrics.server.domain.banner.repository.BannerQueryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class BannerQueryService {

    private final BannerQueryRepository bannerQueryRepository;

    public List<BannerGetResponse> getRecentBanners(
            int size
    ) {
        return bannerQueryRepository.findRecentBanners(size)
                .stream()
                .map(BannerGetResponse::of)
                .toList();
    }
}
