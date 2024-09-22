package com.projectlyrics.server.domain.discipline.repository.impl;

import static com.projectlyrics.server.domain.discipline.domain.QDiscipline.discipline;

import com.projectlyrics.server.domain.discipline.domain.Discipline;
import com.projectlyrics.server.domain.discipline.repository.DisciplineQueryRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QueryDslDisciplineQueryRepository implements DisciplineQueryRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Discipline> findById(Long disciplineId) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(discipline)
                        .leftJoin(discipline.user).fetchJoin()
                        .leftJoin(discipline.artist).fetchJoin()
                        .where(
                                discipline.id.eq(disciplineId),
                                discipline.deletedAt.isNull()
                        )
                        .fetchOne());
    }
}
