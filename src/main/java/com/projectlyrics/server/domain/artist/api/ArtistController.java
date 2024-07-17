package com.projectlyrics.server.domain.artist.api;

import com.projectlyrics.server.domain.artist.dto.request.ArtistAddRequest;
import com.projectlyrics.server.domain.artist.dto.request.ArtistUpdateRequest;
import com.projectlyrics.server.domain.artist.dto.response.ArtistAddResponse;
import com.projectlyrics.server.domain.artist.dto.response.ArtistGetResponse;
import com.projectlyrics.server.domain.artist.dto.response.ArtistUpdateResponse;
import com.projectlyrics.server.domain.artist.service.ArtistCommandService;
import com.projectlyrics.server.domain.artist.service.ArtistQueryService;
import com.projectlyrics.server.domain.common.dto.util.CursorBasePaginatedResponse;
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

@RequiredArgsConstructor
@RequestMapping("/api/v1/artists")
@RestController
public class ArtistController {

    private final ArtistQueryService artistQueryService;
    private final ArtistCommandService artistCommandService;

    @PostMapping
    public ResponseEntity<ArtistAddResponse> addArtist(
            @RequestBody @Valid ArtistAddRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(artistCommandService.addArtist(request));
    }

    @PatchMapping("/{artistId}")
    public ResponseEntity<ArtistUpdateResponse> updateArtist(
            @PathVariable(name = "artistId") Long artistId,
            @RequestBody ArtistUpdateRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(artistCommandService.updateArtist(artistId, request));
    }

    @DeleteMapping("/{artistId}")
    public ResponseEntity<Void> deleteArtist(
            @PathVariable(name = "artistId") Long artistId
    ) {
        artistCommandService.deleteArtist(artistId);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
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
    public ResponseEntity<CursorBasePaginatedResponse<ArtistGetResponse>> getArtistList(
            @RequestParam(name = "cursor", required = false) Long cursor,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(artistQueryService.getArtistList(cursor, PageRequest.ofSize(size)));
    }

    @GetMapping("/search")
    public ResponseEntity<CursorBasePaginatedResponse<ArtistGetResponse>> searchArtist(
            @RequestParam(name = "cursor", required = false) Long cursor,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "query") String query
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(artistQueryService.searchArtists(query, cursor, PageRequest.of(0, size)));
    }
}
