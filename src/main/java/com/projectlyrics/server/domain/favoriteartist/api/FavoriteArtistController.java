package com.projectlyrics.server.domain.favoriteartist.api;

import com.projectlyrics.server.domain.favoriteartist.dto.request.CreateFavoriteArtistListRequest;
import com.projectlyrics.server.domain.favoriteartist.service.FavoriteArtistCommandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/favorite-artists")
@RequiredArgsConstructor
public class FavoriteArtistController {

    private final FavoriteArtistCommandService favoriteArtistCommandService;

    @PostMapping("/batch")
    public ResponseEntity<Void> saveAll(
            Principal principal,
            @RequestBody @Valid CreateFavoriteArtistListRequest request
    ) {
        favoriteArtistCommandService.saveAll(Long.parseLong(principal.getName()), request);
        return ResponseEntity.ok()
                .build();
    }
}
