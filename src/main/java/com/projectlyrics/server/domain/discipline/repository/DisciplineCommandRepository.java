package com.projectlyrics.server.domain.discipline.repository;

import com.projectlyrics.server.domain.discipline.domain.Discipline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DisciplineCommandRepository extends JpaRepository<Discipline, Long> {
}
