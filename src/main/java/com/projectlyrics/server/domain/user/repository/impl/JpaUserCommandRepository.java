package com.projectlyrics.server.domain.user.repository.impl;

import com.projectlyrics.server.domain.user.entity.User;
import org.springframework.data.repository.Repository;

public interface JpaUserCommandRepository extends Repository<User, Long> {

  User save(User entity);
}
