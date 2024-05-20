package com.projectlyrics.server.domain.user.repository.impl;

import com.projectlyrics.server.domain.user.entity.User;
import org.springframework.data.repository.Repository;

public interface JpaCommandUserRepository extends Repository<User, Long> {

  User save(User entity);
}
