package com.projectlyrics.server.domain.song.api;

import com.projectlyrics.server.domain.common.dto.util.CursorBasePaginatedResponse;
import com.projectlyrics.server.domain.common.dto.util.OffsetBasePaginatedResponse;
import com.projectlyrics.server.domain.song.dto.response.SongGetResponse;
import com.projectlyrics.server.domain.song.dto.response.SongSearchResponse;
import com.projectlyrics.server.domain.song.service.SongQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/songs")
@RequiredArgsConstructor
public class SongController {

    private final SongQueryService songQueryService;

    @GetMapping("/search")
    public ResponseEntity<OffsetBasePaginatedResponse<SongSearchResponse>> searchSongs(
            @RequestParam(name = "query", required = false) String query,
            @RequestParam(required = false, defaultValue = "0") int pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(songQueryService.searchSongs(query, pageNumber, pageSize));
    }

    @GetMapping("/search/artists")
    public ResponseEntity<CursorBasePaginatedResponse<SongGetResponse>> searchSongsByArtist(
            @RequestParam(name = "artistId") Long artistId,
            @RequestParam(name = "query", required = false) String query,
            @RequestParam(name = "cursor", required = false) Long cursor,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(songQueryService.searchSongsByArtist(artistId, query, cursor, size));
    }

    @GetMapping("/{songId}")
    public ResponseEntity<SongSearchResponse> getSong(
            @PathVariable(name = "songId") Long songId
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(songQueryService.getSongById(songId));
    }
}
