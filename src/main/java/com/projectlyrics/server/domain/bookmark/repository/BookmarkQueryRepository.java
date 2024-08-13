package com.projectlyrics.server.domain.bookmark.repository;

import com.projectlyrics.server.domain.bookmark.domain.Bookmark;

import java.util.Optional;

public interface BookmarkQueryRepository {

    Optional<Bookmark> findByNoteIdAndUserId(Long noteId, Long userId);
}
