package com.projectlyrics.server.domain.user.repository;

import com.projectlyrics.server.domain.auth.entity.enumerate.AuthProvider;
import com.projectlyrics.server.domain.user.entity.User;
import java.util.Optional;

public interface QueryUserRepository {

  Optional<User> findBySocialIdAndAuthProviderAndNotDeleted(long socialId, AuthProvider authProvider);
}
