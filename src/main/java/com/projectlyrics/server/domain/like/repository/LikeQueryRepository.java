package com.projectlyrics.server.domain.like.repository;

import com.projectlyrics.server.domain.like.domain.Like;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.Lock;

public interface LikeQueryRepository {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Like> findByNoteIdAndUserId(Long noteId, Long userId);

    long countByNoteId(Long noteId);
}
