package com.projectlyrics.server.domain.banner.repository;

import com.projectlyrics.server.domain.banner.domain.Banner;

public interface BannerQueryRepository {
    Banner findById(Long id);
}
