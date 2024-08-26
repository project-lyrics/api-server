package com.projectlyrics.server.domain.user.repository.impl;

import com.projectlyrics.server.domain.user.entity.AuthProvider;
import com.projectlyrics.server.domain.user.entity.QUser;
import com.projectlyrics.server.domain.user.entity.SocialInfo;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.repository.UserQueryRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.projectlyrics.server.domain.user.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class QueryDslUserQueryRepository implements UserQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<User> findBySocialIdAndAuthProvider(String socialId, AuthProvider authProvider) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(user)
                        .where(
                                user.socialInfo.socialId.eq(socialId),
                                user.socialInfo.authProvider.eq(authProvider),
                                user.deletedAt.isNull()
                        )
                        .fetchOne()
        );
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(user)
                        .where(
                                user.id.eq(id),
                                user.deletedAt.isNull()
                        )
                        .fetchOne()
        );
    }

    @Override
    public List<User> findAll() {
        return jpaQueryFactory
                .selectFrom(user)
                .where(
                        user.deletedAt.isNull()
                )
                .fetch();
    }

    @Override
    public boolean existsBySocialInfo(SocialInfo socialInfo) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(user)
                        .where(user.socialInfo.eq(socialInfo))
                        .fetchOne()
        ).isPresent();
    }
}
