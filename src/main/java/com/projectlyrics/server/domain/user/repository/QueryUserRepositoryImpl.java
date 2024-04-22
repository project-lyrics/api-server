package com.projectlyrics.server.domain.user.repository;

import com.projectlyrics.server.domain.user.entity.QUser;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.global.auth.external.AuthProvider;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class QueryUserRepositoryImpl implements QueryUserRepository {

  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public Optional<User> findBySocialIdAndAuthProviderAndNotDeleted(Long socialId, AuthProvider authProvider) {
    return Optional.ofNullable(
        jpaQueryFactory
            .selectFrom(QUser.user)
            .where(
                QUser.user.socialId.eq(socialId),
                QUser.user.authProvider.eq(authProvider),
                QUser.user.commonField.deletedAt.isNull()
            )
            .fetchOne()
    );
  }
}
