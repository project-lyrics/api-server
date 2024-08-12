package com.projectlyrics.server.domain.favoriteartist.api;

import com.projectlyrics.server.domain.auth.authentication.AuthContext;
import com.projectlyrics.server.domain.auth.authentication.Authenticated;
import com.projectlyrics.server.domain.common.dto.util.CursorBasePaginatedResponse;
import com.projectlyrics.server.domain.favoriteartist.dto.FavoriteArtistResponse;
import com.projectlyrics.server.domain.favoriteartist.dto.request.CreateFavoriteArtistListRequest;
import com.projectlyrics.server.domain.favoriteartist.service.FavoriteArtistCommandService;
import com.projectlyrics.server.domain.favoriteartist.service.FavoriteArtistQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/favorite-artists")
@RequiredArgsConstructor
public class FavoriteArtistController {

    private final FavoriteArtistCommandService favoriteArtistCommandService;
    private final FavoriteArtistQueryService favoriteArtistQueryService;

    @PostMapping("/batch")
    public ResponseEntity<Void> saveAll(
            @Authenticated AuthContext authContext,
            @RequestBody @Valid CreateFavoriteArtistListRequest request
    ) {
        favoriteArtistCommandService.saveAll(authContext.getId(), request);
        return ResponseEntity.ok()
                .build();
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(
            @Authenticated AuthContext authContext,
            @RequestParam(name = "artistId") Long artistId
    ) {
        favoriteArtistCommandService.delete(authContext.getId(), artistId);
        return ResponseEntity.ok()
                .build();
    }

    @GetMapping
    public ResponseEntity<CursorBasePaginatedResponse<FavoriteArtistResponse>> findAll(
            @Authenticated AuthContext authContext,
            @RequestParam(name = "cursor", required = false) Long cursorId,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        CursorBasePaginatedResponse<FavoriteArtistResponse> response = favoriteArtistQueryService.findFavoriteArtists
                (authContext.getId(),
                        cursorId,
                        PageRequest.of(0, size)
                );
        return ResponseEntity.ok(response);
    }
}
