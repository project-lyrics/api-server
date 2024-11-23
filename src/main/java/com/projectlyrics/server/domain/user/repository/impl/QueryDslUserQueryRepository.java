package com.projectlyrics.server.domain.user.repository.impl;

import static com.projectlyrics.server.domain.block.domain.QBlock.block;
import static com.projectlyrics.server.domain.user.entity.QUser.user;


import com.projectlyrics.server.domain.block.domain.Block;
import com.projectlyrics.server.domain.common.entity.enumerate.EntityStatusEnum;
import com.projectlyrics.server.domain.user.entity.AuthProvider;
import com.projectlyrics.server.domain.user.entity.SocialInfo;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.repository.UserQueryRepository;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
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
    public Optional<User> findDeletedBySocialIdAndAuthProvider(String socialId, AuthProvider authProvider) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(user)
                        .where(
                                user.socialInfo.socialId.eq(socialId),
                                user.socialInfo.authProvider.eq(authProvider),
                                user.status.eq(EntityStatusEnum.DELETED),
                                user.deletedAt.isNotNull()
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
    public List<User> findAllBlocked(Long id) {
        return jpaQueryFactory
                .select(block.blocked)
                .from(block)
                .join(user).on(block.blocker.id.eq(id))
                .where(
                        block.blocker.id.eq(id),
                        block.deletedAt.isNull()
                )
                .fetch();
    }

    @Override
    public boolean existsBySocialInfo(SocialInfo socialInfo) {
        return jpaQueryFactory
                .selectFrom(user)
                .where(
                        user.socialInfo.eq(socialInfo),
                        user.status.in(EntityStatusEnum.YET, EntityStatusEnum.IN_USE)
                )
                .fetchFirst() != null;
    }

    @Override
    public boolean existsBySocialInfoAndForceDelete(SocialInfo socialInfo) {
        return jpaQueryFactory
                .selectFrom(user)
                .where(
                        user.socialInfo.eq(socialInfo),
                        user.status.eq(EntityStatusEnum.FORCED_WITHDRAWAL)
                )
                .fetchFirst() != null;
    }
}
