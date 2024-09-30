package com.projectlyrics.server.domain.discipline.repository;

import com.projectlyrics.server.domain.discipline.domain.Discipline;
import java.util.Optional;

public interface DisciplineQueryRepository {
    Optional<Discipline> findById(Long disciplineId);
    Boolean existsDisciplineOfAll(Long userId);
    Boolean existsDisciplineOfArtist(Long artistId, Long userId);
}
