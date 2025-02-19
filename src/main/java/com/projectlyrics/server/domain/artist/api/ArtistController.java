package com.projectlyrics.server.domain.artist.api;

import com.projectlyrics.server.domain.artist.dto.request.ArtistCreateRequest;
import com.projectlyrics.server.domain.artist.dto.request.ArtistUpdateRequest;
import com.projectlyrics.server.domain.artist.dto.response.ArtistGetResponse;
import com.projectlyrics.server.domain.artist.service.ArtistCommandService;
import com.projectlyrics.server.domain.artist.service.ArtistQueryService;
import com.projectlyrics.server.domain.auth.authentication.AuthContext;
import com.projectlyrics.server.domain.auth.authentication.Authenticated;
import com.projectlyrics.server.domain.common.dto.util.OffsetBasePaginatedResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RequiredArgsConstructor
@RequestMapping("/api/v1/artists")
@RestController
public class ArtistController {

    private final ArtistQueryService artistQueryService;
    private final ArtistCommandService artistCommandService;

    @PostMapping
    public ResponseEntity<Void> create(
            @RequestBody @Valid ArtistCreateRequest request
    ) {
        artistCommandService.create(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @PatchMapping("/{artistId}")
    public ResponseEntity<Void> update(
            @PathVariable(name = "artistId") Long artistId,
            @RequestBody ArtistUpdateRequest request
    ) {
        artistCommandService.update(artistId, request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @DeleteMapping("/{artistId}")
    public ResponseEntity<Void> delete(
            @Authenticated AuthContext authContext,
            @PathVariable(name = "artistId") Long artistId
    ) {
        artistCommandService.delete(artistId, authContext.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @GetMapping("/{artistId}")
    public ResponseEntity<ArtistGetResponse> getArtist(
            @PathVariable(name = "artistId") Long artistId
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ArtistGetResponse.of(artistQueryService.getArtistById(artistId)));
    }

    @GetMapping
    public ResponseEntity<OffsetBasePaginatedResponse<ArtistGetResponse>> getArtistList(
            @RequestParam(name = "pageNumber", required = false) int page,
            @RequestParam(name = "pageSize", defaultValue = "12") int size
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(artistQueryService.getArtistList(PageRequest.of(page, size)));
    }

    @GetMapping("/search")
    public ResponseEntity<OffsetBasePaginatedResponse<ArtistGetResponse>> searchArtist(
            @RequestParam(name = "pageNumber", required = false) int page,
            @RequestParam(name = "pageSize", defaultValue = "12") int size,
            @RequestParam(name = "query", required = false) String query
    ) {
        if (Objects.isNull(query) || query.isEmpty()) {
            ResponseEntity
                    .status(HttpStatus.OK)
                    .body(artistQueryService.getArtistList(PageRequest.of(0, size)));
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(artistQueryService.searchArtists(query, PageRequest.of(page, size)));
    }
}
