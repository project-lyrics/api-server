package com.projectlyrics.server.domain.view.repository.impl;

import static com.projectlyrics.server.domain.view.domain.QView.view;

import com.projectlyrics.server.domain.view.domain.View;
import com.projectlyrics.server.domain.view.exception.ViewNotFoundException;
import com.projectlyrics.server.domain.view.repository.ViewQueryRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QueryDslViewQueryRepository implements ViewQueryRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public View findById(Long id) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(view)
                        .leftJoin(view.note).fetchJoin()
                        .leftJoin(view.user).fetchJoin()
                        .where(
                                view.id.eq(id),
                                view.deletedAt.isNull()
                        )
                        .fetchOne()
        ).orElseThrow(ViewNotFoundException::new);
    }
}
