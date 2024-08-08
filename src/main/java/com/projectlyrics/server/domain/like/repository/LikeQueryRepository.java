package com.projectlyrics.server.domain.like.repository;

import com.projectlyrics.server.domain.like.domain.Like;

import java.util.Optional;

public interface LikeQueryRepository {

    Optional<Like> findByNoteIdAndUserId(Long noteId, Long userId);

    long countByNoteId(Long noteId);
}
