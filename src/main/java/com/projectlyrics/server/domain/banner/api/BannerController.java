package com.projectlyrics.server.domain.banner.api;

import com.projectlyrics.server.domain.banner.dto.request.BannerCreateRequest;
import com.projectlyrics.server.domain.banner.dto.response.BannerCreateResponse;
import com.projectlyrics.server.domain.banner.service.BannerCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/banners")
@RequiredArgsConstructor
public class BannerController {
    private final BannerCommandService bannerCommandService;

    @PostMapping
    public ResponseEntity<BannerCreateResponse> create(
            @RequestBody BannerCreateRequest request
    ) {
        bannerCommandService.create(request);

        return ResponseEntity
                .ok(new BannerCreateResponse(true));
    }
}
