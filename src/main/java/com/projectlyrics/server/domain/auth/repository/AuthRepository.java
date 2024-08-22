package com.projectlyrics.server.domain.auth.repository;

import com.projectlyrics.server.domain.auth.domain.Auth;
import org.springframework.data.repository.CrudRepository;

public interface AuthRepository extends CrudRepository<Auth, String> {
}
