package com.projectlyrics.server.domain.user.repository;

import com.projectlyrics.server.domain.user.entity.AuthProvider;
import com.projectlyrics.server.domain.user.entity.SocialInfo;
import com.projectlyrics.server.domain.user.entity.User;

import java.util.Optional;

public interface UserQueryRepository {

    Optional<User> findBySocialIdAndAuthProviderAndNotDeleted(String socialId, AuthProvider authProvider);

    Optional<User> findById(Long id);

    boolean existsBySocialInfo(SocialInfo socialInfo);
}
