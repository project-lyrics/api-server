package com.projectlyrics.server.domain.banner.repository;

import com.projectlyrics.server.domain.banner.domain.Banner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BannerCommandRepository extends JpaRepository<Banner, Long> {
}
