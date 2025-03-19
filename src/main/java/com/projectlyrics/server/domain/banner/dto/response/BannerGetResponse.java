package com.projectlyrics.server.domain.banner.dto.response;

import com.projectlyrics.server.domain.banner.domain.Banner;

public record BannerGetResponse (
        Long id,
        String imageUrl,
        String redirectUrl
){
    public static BannerGetResponse of(Banner banner) {
        return new BannerGetResponse(
                banner.getId(),
                banner.getImageUrl(),
                banner.getRedirectUrl()
        );
    }
}
