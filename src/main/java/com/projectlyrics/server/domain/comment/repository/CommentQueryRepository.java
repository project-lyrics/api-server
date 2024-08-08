package com.projectlyrics.server.domain.comment.repository;

import com.projectlyrics.server.domain.comment.domain.Comment;

import java.util.Optional;

public interface CommentQueryRepository {

    Optional<Comment> findById(Long id);
}
