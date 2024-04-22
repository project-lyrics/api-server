package com.projectlyrics.server.domain.user.repository;

import com.projectlyrics.server.domain.user.entity.User;
import org.springframework.data.repository.Repository;

public interface CommandQueryUserRepository extends Repository<User, Long>, QueryUserRepository {

  User save(User entity);
}
