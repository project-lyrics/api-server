package com.projectlyrics.server.domain.view.repository;

import com.projectlyrics.server.domain.view.domain.View;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ViewCommandRepository extends JpaRepository<View, Long> {
}
