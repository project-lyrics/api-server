package com.projectlyrics.server.domain.bookmark.api;

import com.projectlyrics.server.domain.auth.authentication.AuthContext;
import com.projectlyrics.server.domain.auth.authentication.Authenticated;
import com.projectlyrics.server.domain.bookmark.dto.response.BookmarkResponse;
import com.projectlyrics.server.domain.bookmark.service.BookmarkCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkCommandService bookmarkCommandService;

    @PostMapping
    public ResponseEntity<BookmarkResponse> create(
            @Authenticated AuthContext authContext,
            @RequestParam(name = "noteId") Long noteId
    ) {
        bookmarkCommandService.create(noteId, authContext.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BookmarkResponse.of(noteId));
    }

    @DeleteMapping
    public ResponseEntity<BookmarkResponse> delete(
            @Authenticated AuthContext authContext,
            @RequestParam(name = "noteId") Long noteId
    ) {
        bookmarkCommandService.delete(noteId, authContext.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BookmarkResponse.of(noteId));
    }
}
