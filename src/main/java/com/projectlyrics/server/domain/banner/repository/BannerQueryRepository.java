package com.projectlyrics.server.domain.banner.repository;

import com.projectlyrics.server.domain.banner.domain.Banner;
import java.util.List;

public interface BannerQueryRepository {
    Banner findById(Long id);

    List<Banner> findRecentBanners(int size);
}
