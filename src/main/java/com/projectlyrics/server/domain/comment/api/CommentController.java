package com.projectlyrics.server.domain.comment.api;

import com.projectlyrics.server.domain.auth.authentication.AuthContext;
import com.projectlyrics.server.domain.auth.authentication.Authenticated;
import com.projectlyrics.server.domain.comment.dto.request.CommentCreateRequest;
import com.projectlyrics.server.domain.comment.dto.request.CommentUpdateRequest;
import com.projectlyrics.server.domain.comment.dto.response.CommentCreateResponse;
import com.projectlyrics.server.domain.comment.dto.response.CommentDeleteResponse;
import com.projectlyrics.server.domain.comment.dto.response.CommentUpdateResponse;
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
    public ResponseEntity<CommentCreateResponse> create(
            @Authenticated AuthContext authContext,
            @RequestBody @Valid CommentCreateRequest request
    ) {
        commentCommandService.create(request, authContext.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new CommentCreateResponse(true));
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentUpdateResponse> update(
            @Authenticated AuthContext authContext,
            @PathVariable(name = "commentId") Long commentId,
            @RequestBody @Valid CommentUpdateRequest request
    ) {
        commentCommandService.update(request, commentId, authContext.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new CommentUpdateResponse(true));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<CommentDeleteResponse> delete(
            @Authenticated AuthContext authContext,
            @PathVariable(name = "commentId") Long commentId
    ) {
        commentCommandService.delete(authContext.getId(), commentId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new CommentDeleteResponse(true));
    }
}
