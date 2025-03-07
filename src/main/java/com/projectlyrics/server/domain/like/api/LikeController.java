package com.projectlyrics.server.domain.like.api;

import com.projectlyrics.server.domain.auth.authentication.AuthContext;
import com.projectlyrics.server.domain.auth.authentication.Authenticated;
import com.projectlyrics.server.domain.like.dto.response.LikesResponse;
import com.projectlyrics.server.domain.like.service.LikeCommandService;
import com.projectlyrics.server.domain.like.service.LikeQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeCommandService likeCommandService;
    private final LikeQueryService likeQueryService;

    @PostMapping
    public ResponseEntity<LikesResponse> create(
            @Authenticated AuthContext authContext,
            @RequestParam(name = "noteId") Long noteId
    ) {
        likeCommandService.create(noteId, authContext.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(LikesResponse.of(
                        likeQueryService.countLikesOfNote(noteId),
                        noteId
                ));
    }

    @DeleteMapping
    public ResponseEntity<LikesResponse> delete(
            @Authenticated AuthContext authContext,
            @RequestParam(name = "noteId") Long noteId
    ) {
        likeCommandService.delete(noteId, authContext.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(LikesResponse.of(
                        likeQueryService.countLikesOfNote(noteId),
                        noteId
                ));
    }
}
