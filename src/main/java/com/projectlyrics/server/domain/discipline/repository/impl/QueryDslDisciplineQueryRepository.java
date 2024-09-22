package com.projectlyrics.server.domain.discipline.repository.impl;

import com.projectlyrics.server.domain.discipline.repository.DisciplineQueryRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QueryDslDisciplineQueryRepository implements DisciplineQueryRepository {
    private final JPAQueryFactory jpaQueryFactory;
}
