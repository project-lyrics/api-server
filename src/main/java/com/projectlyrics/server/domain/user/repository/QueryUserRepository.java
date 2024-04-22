package com.projectlyrics.server.domain.user.repository;

import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.global.auth.external.AuthProvider;
import java.util.Optional;

public interface QueryUserRepository {

  Optional<User> findBySocialIdAndAuthProviderAndNotDeleted(Long socialId, AuthProvider authProvider);
}
