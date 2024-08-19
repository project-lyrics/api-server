package com.projectlyrics.server.domain.user.repository.impl;

import com.projectlyrics.server.domain.user.entity.AuthProvider;
import com.projectlyrics.server.domain.user.entity.QUser;
import com.projectlyrics.server.domain.user.entity.SocialInfo;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.repository.UserQueryRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QueryDslUserQueryRepository implements UserQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<User> findBySocialIdAndAuthProvider(String socialId, AuthProvider authProvider) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(QUser.user)
                        .where(
                                QUser.user.socialInfo.socialId.eq(socialId),
                                QUser.user.socialInfo.authProvider.eq(authProvider),
                                QUser.user.deletedAt.isNull()
                        )
                        .fetchOne()
        );
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(QUser.user)
                        .where(
                                QUser.user.id.eq(id),
                                QUser.user.deletedAt.isNull()
                        )
                        .fetchOne()
        );
    }

    @Override
    public boolean existsBySocialInfo(SocialInfo socialInfo) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(QUser.user)
                        .where(QUser.user.socialInfo.eq(socialInfo))
                        .fetchOne()
        ).isPresent();
    }
}
