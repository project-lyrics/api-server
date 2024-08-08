package com.projectlyrics.server.domain.comment.api;

import com.projectlyrics.server.domain.auth.authentication.AuthContext;
import com.projectlyrics.server.domain.auth.authentication.Authenticated;
import com.projectlyrics.server.domain.comment.dto.request.CommentCreateRequest;
import com.projectlyrics.server.domain.comment.dto.request.CommentUpdateRequest;
import com.projectlyrics.server.domain.comment.service.CommentCommandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentCommandService commentCommandService;

    @PostMapping
    public ResponseEntity<Void> create(
            @Authenticated AuthContext authContext,
            @RequestBody @Valid CommentCreateRequest request
    ) {
        commentCommandService.create(request, authContext.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<Void> update(
            @Authenticated AuthContext authContext,
            @PathVariable(name = "commentId") Long commentId,
            @RequestBody @Valid CommentUpdateRequest request
    ) {
        commentCommandService.update(request, commentId, authContext.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(
            @Authenticated AuthContext authContext,
            @PathVariable(name = "commentId") Long commentId
    ) {
        commentCommandService.delete(authContext.getId(), commentId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }
}
