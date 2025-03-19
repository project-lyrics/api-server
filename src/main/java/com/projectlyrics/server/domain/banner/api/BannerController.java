package com.projectlyrics.server.domain.banner.api;

import com.projectlyrics.server.domain.banner.dto.request.BannerCreateRequest;
import com.projectlyrics.server.domain.banner.dto.response.BannerCreateResponse;
import com.projectlyrics.server.domain.banner.dto.response.BannerGetResponse;
import com.projectlyrics.server.domain.banner.service.BannerCommandService;
import com.projectlyrics.server.domain.banner.service.BannerQueryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/banners")
@RequiredArgsConstructor
public class BannerController {

    private final BannerCommandService bannerCommandService;
    private final BannerQueryService bannerQueryService;

    @PostMapping
    public ResponseEntity<BannerCreateResponse> create(
            @RequestBody BannerCreateRequest request
    ) {
        bannerCommandService.create(request);

        return ResponseEntity
                .ok(new BannerCreateResponse(true));
    }

    @GetMapping
    public ResponseEntity<List<BannerGetResponse>> getAll(
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(bannerQueryService.getRecentBanners(size));
    }
}
