package com.projectlyrics.server.domain.comment.repository;

import com.projectlyrics.server.domain.comment.domain.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Optional;

public interface CommentQueryRepository {

    Optional<Comment> findById(Long id);

    Slice<Comment> findAllByNoteId(Long noteId, Long cursorId, Pageable pageable);

    long countByNoteId(Long noteId);
}
