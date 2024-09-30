package com.projectlyrics.server.domain.discipline.repository.impl;

import static com.projectlyrics.server.domain.discipline.domain.QDiscipline.discipline;

import com.projectlyrics.server.domain.discipline.domain.Discipline;
import com.projectlyrics.server.domain.discipline.domain.DisciplineType;
import com.projectlyrics.server.domain.discipline.repository.DisciplineQueryRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
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

    @Override
    public Boolean existsDisciplineOfAll(Long userId) {
        return jpaQueryFactory
                .selectOne()
                .from(discipline)
                .where(
                        discipline.user.id.eq(userId),
                        discipline.type.in(DisciplineType.ALL_3DAYS, DisciplineType.ALL_14DAYS, DisciplineType.ALL_30DAYS, DisciplineType.ALL_3MONTHS),
                        discipline.startTime.before(LocalDateTime.now()),
                        discipline.endTime.after(LocalDateTime.now()),
                        discipline.deletedAt.isNull()
                )
                .fetchFirst() != null;
    }

    @Override
    public Boolean existsDisciplineOfArtist(Long artistId, Long userId) {
        return jpaQueryFactory
                .selectOne()
                .from(discipline)
                .where(
                        discipline.user.id.eq(userId),
                        discipline.artist.id.eq(artistId),
                        discipline.type.in(DisciplineType.ARTIST_3DAYS, DisciplineType.ARTIST_14DAYS, DisciplineType.ARTIST_30DAYS, DisciplineType.ARTIST_3MONTHS),
                        discipline.startTime.before(LocalDateTime.now()),
                        discipline.endTime.after(LocalDateTime.now()),
                        discipline.deletedAt.isNull()
                )
                .fetchFirst() != null;
    }
}
