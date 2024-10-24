package com.projectlyrics.server.domain.favoriteartist.api;

import com.projectlyrics.server.domain.auth.authentication.AuthContext;
import com.projectlyrics.server.domain.auth.authentication.Authenticated;
import com.projectlyrics.server.domain.common.dto.util.CursorBasePaginatedResponse;
import com.projectlyrics.server.domain.favoriteartist.dto.response.FavoriteArtistCreateResponse;
import com.projectlyrics.server.domain.favoriteartist.dto.response.FavoriteArtistDeleteResponse;
import com.projectlyrics.server.domain.favoriteartist.dto.response.FavoriteArtistExistsResponse;
import com.projectlyrics.server.domain.favoriteartist.dto.response.FavoriteArtistResponse;
import com.projectlyrics.server.domain.favoriteartist.dto.request.CreateFavoriteArtistListRequest;
import com.projectlyrics.server.domain.favoriteartist.service.FavoriteArtistCommandService;
import com.projectlyrics.server.domain.favoriteartist.service.FavoriteArtistQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/favorite-artists")
@RequiredArgsConstructor
public class FavoriteArtistController {

    private final FavoriteArtistCommandService favoriteArtistCommandService;
    private final FavoriteArtistQueryService favoriteArtistQueryService;

    @PostMapping
    public ResponseEntity<FavoriteArtistCreateResponse> create(
            @Authenticated AuthContext authContext,
            @RequestParam(name = "artistId") Long artistId
    ) {
        favoriteArtistCommandService.create(authContext.getId(), artistId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new FavoriteArtistCreateResponse(true));
    }

    @PostMapping("/batch")
    public ResponseEntity<FavoriteArtistCreateResponse> createAll(
            @Authenticated AuthContext authContext,
            @RequestBody @Valid CreateFavoriteArtistListRequest request
    ) {
        favoriteArtistCommandService.createAll(authContext.getId(), request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new FavoriteArtistCreateResponse(true));
    }

    @DeleteMapping
    public ResponseEntity<FavoriteArtistDeleteResponse> delete(
            @Authenticated AuthContext authContext,
            @RequestParam(name = "artistId") Long artistId
    ) {
        favoriteArtistCommandService.delete(authContext.getId(), artistId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new FavoriteArtistDeleteResponse(true));
    }

    @GetMapping
    public ResponseEntity<CursorBasePaginatedResponse<FavoriteArtistResponse>> findAll(
            @Authenticated AuthContext authContext,
            @RequestParam(name = "cursor", required = false) Long cursorId,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        CursorBasePaginatedResponse<FavoriteArtistResponse> response = favoriteArtistQueryService.findFavoriteArtists(
                authContext.getId(),
                cursorId,
                PageRequest.ofSize(size)
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("/having-notes")
    public ResponseEntity<List<FavoriteArtistResponse>> getAllHavingNotesOfUser(
            @Authenticated AuthContext authContext
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(favoriteArtistQueryService.findAllHavingNotesOfUser(authContext.getId()));
    }

    @GetMapping("/bookmarked")
    public ResponseEntity<List<FavoriteArtistResponse>> getAllBookmarked(
            @Authenticated AuthContext authContext
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(favoriteArtistQueryService.findAllBookmarked(authContext.getId()));
    }

    @GetMapping("/exists")
    public ResponseEntity<FavoriteArtistExistsResponse> exists(
            @Authenticated AuthContext authContext,
            @RequestParam(name = "artistId") Long artistId
    ) {
        boolean result = favoriteArtistQueryService.existsByUserIdAndArtistId(authContext.getId(), artistId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new FavoriteArtistExistsResponse(result));
    }
}
