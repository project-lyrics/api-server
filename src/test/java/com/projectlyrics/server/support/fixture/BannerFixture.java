package com.projectlyrics.server.support.fixture;

import com.projectlyrics.server.domain.banner.domain.Banner;
import com.projectlyrics.server.domain.banner.domain.BannerCreate;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class BannerFixture extends BaseFixture{
    private Long id;
    private String imageUrl = "imageUrl";
    private String redirectUrl = "redirectUrl";
    private LocalDateTime dueDate = LocalDate.now().atTime(23, 59, 59);

    public static Banner create() {
        return Banner.createWithId(
                getUniqueId(),
                new BannerCreate(
                        "imageUrl",
                        "redirectUrl",
                        LocalDate.now().minusDays(1).atTime(23, 59, 59),
                        LocalDate.now().plusDays(1).atTime(23, 59, 59)
                )
        );
    }
}
