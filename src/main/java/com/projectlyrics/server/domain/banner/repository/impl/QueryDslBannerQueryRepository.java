package com.projectlyrics.server.domain.banner.repository.impl;

import static com.projectlyrics.server.domain.banner.domain.QBanner.banner;

import com.projectlyrics.server.domain.banner.domain.Banner;
import com.projectlyrics.server.domain.banner.exception.BannerNotFoundException;
import com.projectlyrics.server.domain.banner.repository.BannerQueryRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QueryDslBannerQueryRepository implements BannerQueryRepository {

    private  final JPAQueryFactory jpaQueryFactory;

    @Override
    public Banner findById(Long id) {
        return Optional.of(
                jpaQueryFactory
                        .selectFrom(banner)
                        .where(banner.id.eq(id),
                                banner.deletedAt.isNull())
                        .fetchFirst()
        ).orElseThrow(BannerNotFoundException::new);
    }

    @Override
    public List<Banner> findRecentBanners(int size) {
        return jpaQueryFactory
                .selectFrom(banner)
                .where(
                        banner.dueDate.isNull().or(banner.dueDate.after(LocalDateTime.now())),
                        banner.deletedAt.isNull()
                )
                .orderBy(banner.id.desc())
                .limit(size)
                .fetch();
    }
}
