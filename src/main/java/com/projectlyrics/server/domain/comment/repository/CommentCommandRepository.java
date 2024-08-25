package com.projectlyrics.server.domain.comment.repository;

import com.projectlyrics.server.domain.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentCommandRepository extends JpaRepository<Comment, Long> {

    void deleteAllByWriterId(Long writerId);
}
