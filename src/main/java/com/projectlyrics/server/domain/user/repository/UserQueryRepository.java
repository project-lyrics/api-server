package com.projectlyrics.server.domain.user.repository;

import com.projectlyrics.server.domain.auth.entity.enumerate.AuthProvider;
import com.projectlyrics.server.domain.user.entity.User;
import java.util.Optional;

public interface UserQueryRepository {

  Optional<User> findBySocialIdAndAuthProviderAndNotDeleted(String socialId, AuthProvider authProvider);

  Optional<User> findById(Long id);
}
