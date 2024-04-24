package com.projectlyrics.server.domain.user.repository;

import com.projectlyrics.server.domain.user.entity.User;
import org.springframework.data.repository.Repository;

public interface UserCommandRepository extends Repository<User, Long> {

  User save(User entity);
}
