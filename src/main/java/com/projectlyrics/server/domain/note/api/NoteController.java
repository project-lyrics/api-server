package com.projectlyrics.server.domain.note.api;

import com.projectlyrics.server.domain.auth.authentication.AuthContext;
import com.projectlyrics.server.domain.auth.authentication.Authenticated;
import com.projectlyrics.server.domain.common.dto.util.CursorBasePaginatedResponse;
import com.projectlyrics.server.domain.note.dto.request.NoteCreateRequest;
import com.projectlyrics.server.domain.note.dto.request.NoteUpdateRequest;
import com.projectlyrics.server.domain.note.dto.response.NoteCreateResponse;
import com.projectlyrics.server.domain.note.dto.response.NoteDeleteResponse;
import com.projectlyrics.server.domain.note.dto.response.NoteDetailResponse;
import com.projectlyrics.server.domain.note.dto.response.NoteGetResponse;
import com.projectlyrics.server.domain.note.dto.response.NoteUpdateResponse;
import com.projectlyrics.server.domain.note.service.NoteCommandService;
import com.projectlyrics.server.domain.note.service.NoteQueryService;
import com.projectlyrics.server.domain.view.service.ViewCommandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteCommandService noteCommandService;
    private final NoteQueryService noteQueryService;
    private final ViewCommandService viewCommandService;

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
            @RequestHeader("Device-Id") String deviceId,
            @PathVariable(name = "noteId") Long noteId
    ) {
        if (authContext.isAnonymous()) {
            viewCommandService.create(noteId, deviceId);
        } else {
            viewCommandService.create(noteId, authContext.getId(), deviceId);
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(noteQueryService.getNoteById(noteId, authContext.getId()));
    }

    @GetMapping
    public ResponseEntity<CursorBasePaginatedResponse<NoteGetResponse>> getNotesOfUser(
            @Authenticated AuthContext authContext,
            @RequestParam(name = "hasLyrics") boolean hasLyrics,
            @RequestParam(name = "artistId", required = false) Long artistId,
            @RequestParam(name = "cursor", required = false) Long cursor,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        CursorBasePaginatedResponse<NoteGetResponse> response = noteQueryService.getNotesByUserId(hasLyrics, artistId, authContext.getId(), cursor, size);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("/favorite-artists")
    public ResponseEntity<CursorBasePaginatedResponse<NoteGetResponse>> getNotesOfFavoriteArtists(
            @Authenticated AuthContext authContext,
            @RequestParam(name = "hasLyrics") boolean hasLyrics,
            @RequestParam(name = "cursor", required = false) Long cursor,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        CursorBasePaginatedResponse<NoteGetResponse> response = noteQueryService.getNotesOfFavoriteArtists(hasLyrics, authContext.getId(), cursor, size);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("/artists")
    public ResponseEntity<CursorBasePaginatedResponse<NoteGetResponse>> getNotesOfArtist(
            @Authenticated AuthContext authContext,
            @RequestParam(name = "hasLyrics") boolean hasLyrics,
            @RequestParam(name = "artistId") Long artistId,
            @RequestParam(name = "cursor", required = false) Long cursor,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        CursorBasePaginatedResponse<NoteGetResponse> response = noteQueryService.getNotesByArtistId(hasLyrics, artistId, authContext.getId(), cursor, size);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("/songs")
    public ResponseEntity<CursorBasePaginatedResponse<NoteGetResponse>> getNotesOfSong(
            @Authenticated AuthContext authContext,
            @RequestParam(name = "hasLyrics") boolean hasLyrics,
            @RequestParam(name = "songId") Long songId,
            @RequestParam(name = "cursor", required = false) Long cursor,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        CursorBasePaginatedResponse<NoteGetResponse> response = noteQueryService.getNotesBySongId(hasLyrics, songId, authContext.getId(), cursor, size);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("/bookmarked")
    public ResponseEntity<CursorBasePaginatedResponse<NoteGetResponse>> getNotesBookmarked(
            @Authenticated AuthContext authContext,
            @RequestParam(name = "hasLyrics") boolean hasLyrics,
            @RequestParam(name = "artistId", required = false) Long artistId,
            @RequestParam(name = "cursor", required = false) Long cursor,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        CursorBasePaginatedResponse<NoteGetResponse> response = noteQueryService.getBookmarkedNotes(hasLyrics, artistId, authContext.getId(), cursor, size);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}
