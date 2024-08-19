package com.projectlyrics.server.domain.note.api;

import com.projectlyrics.server.domain.auth.authentication.AuthContext;
import com.projectlyrics.server.domain.auth.authentication.Authenticated;
import com.projectlyrics.server.domain.common.dto.util.CursorBasePaginatedResponse;
import com.projectlyrics.server.domain.note.dto.request.NoteCreateRequest;
import com.projectlyrics.server.domain.note.dto.request.NoteUpdateRequest;
import com.projectlyrics.server.domain.note.dto.response.*;
import com.projectlyrics.server.domain.note.service.NoteCommandService;
import com.projectlyrics.server.domain.note.service.NoteQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteCommandService noteCommandService;
    private final NoteQueryService noteQueryService;

    @PostMapping
    public ResponseEntity<NoteCreateResponse> create(
            @Authenticated AuthContext authContext,
            @RequestBody @Valid NoteCreateRequest request
    ) {
        noteCommandService.create(request, authContext.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new NoteCreateResponse(true));
    }

    @PatchMapping("/{noteId}")
    public ResponseEntity<NoteUpdateResponse> update(
            @Authenticated AuthContext authContext,
            @PathVariable(name = "noteId") Long noteId,
            @RequestBody @Valid NoteUpdateRequest request
    ) {
        noteCommandService.update(request, noteId, authContext.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new NoteUpdateResponse(true));
    }

    @DeleteMapping("/{noteId}")
    public ResponseEntity<NoteDeleteResponse> delete(
            @Authenticated AuthContext authContext,
            @PathVariable(name = "noteId") Long noteId
    ) {
        noteCommandService.delete(authContext.getId(), noteId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new NoteDeleteResponse(true));
    }

    @GetMapping("/{noteId}")
    public ResponseEntity<NoteDetailResponse> getNote(
            @Authenticated AuthContext authContext,
            @PathVariable(name = "noteId") Long noteId
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(noteQueryService.getNoteById(authContext.getId(), noteId));
    }

    @GetMapping
    public ResponseEntity<CursorBasePaginatedResponse<NoteGetResponse>> getNotesOfUser(
            @Authenticated AuthContext authContext,
            @RequestParam(name = "cursor", required = false) Long cursor,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        CursorBasePaginatedResponse<NoteGetResponse> response = noteQueryService.getNotesByUserId(authContext.getId(), cursor, size);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("/favorite-artists")
    public ResponseEntity<CursorBasePaginatedResponse<NoteGetResponse>> getNotesOfFavoriteArtists(
            @Authenticated AuthContext authContext,
            @RequestParam(name = "cursor", required = false) Long cursor,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        CursorBasePaginatedResponse<NoteGetResponse> response = noteQueryService.getNotesOfFavoriteArtists(authContext.getId(), cursor, size);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("/artists")
    public ResponseEntity<CursorBasePaginatedResponse<NoteGetResponse>> getNotesOfArtist(
            @Authenticated AuthContext authContext,
            @RequestParam(name = "artistId") Long artistId,
            @RequestParam(name = "hasLyrics") boolean hasLyrics,
            @RequestParam(name = "cursor", required = false) Long cursor,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        CursorBasePaginatedResponse<NoteGetResponse> response = noteQueryService.getNotesByArtistId(authContext.getId(), artistId, hasLyrics, cursor, size);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("/bookmarked")
    public ResponseEntity<CursorBasePaginatedResponse<NoteGetResponse>> getNotesBookmarked(
            @Authenticated AuthContext authContext,
            @RequestParam(name = "artistId", required = false) Long artistId,
            @RequestParam(name = "cursor", required = false) Long cursor,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        CursorBasePaginatedResponse<NoteGetResponse> response = noteQueryService.getBookmarkedNotes(artistId, authContext.getId(), cursor, size);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}
