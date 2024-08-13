package com.projectlyrics.server.domain.bookmark.repository;

import com.projectlyrics.server.domain.bookmark.domain.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookmarkCommandRepository extends JpaRepository<Bookmark, Long> {
}
