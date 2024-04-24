package com.projectlyrics.server.domain.user.repository;

import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.auth.external.AuthProvider;
import java.util.Optional;

public interface UserQueryRepository {

  Optional<User> findBySocialIdAndAuthProviderAndNotDeleted(long socialId, AuthProvider authProvider);
}
