package com.projectlyrics.server.domain.song.api;

import com.projectlyrics.server.domain.common.dto.util.CursorBasePaginatedResponse;
import com.projectlyrics.server.domain.song.dto.response.SongGetResponse;
import com.projectlyrics.server.domain.song.service.SongQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping("/api/v1/songs")
@RequiredArgsConstructor
public class SongController {

    private final SongQueryService songQueryService;

    @GetMapping("/search")
    public ResponseEntity<CursorBasePaginatedResponse<SongGetResponse>> searchSongs(
            @RequestParam(name = "query") String query,
            @RequestParam(name = "cursorId", required = false) Long cursorId,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        if (!StringUtils.hasText(query)) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new CursorBasePaginatedResponse<>(null, false, Collections.emptyList()));
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(songQueryService.searchSongs(query, cursorId, size));
    }
}
