package com.projectlyrics.server.domain.like.repository;

import com.projectlyrics.server.domain.like.domain.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeCommandRepository extends JpaRepository<Like, Long> {
}
