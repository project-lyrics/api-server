package com.projectlyrics.server.domain.user.repository.impl;

import com.projectlyrics.server.domain.auth.entity.enumerate.AuthProvider;
import com.projectlyrics.server.domain.user.entity.QUser;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.repository.QueryUserRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QueryDslQueryUserRepository implements QueryUserRepository {

  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public Optional<User> findBySocialIdAndAuthProviderAndNotDeleted(String socialId, AuthProvider authProvider) {
    return Optional.ofNullable(
        jpaQueryFactory
            .selectFrom(QUser.user)
            .where(
                QUser.user.auth.socialId.eq(socialId),
                QUser.user.auth.authProvider.eq(authProvider),
                QUser.user.commonField.deletedAt.isNull()
            )
            .fetchOne()
    );
  }
}
