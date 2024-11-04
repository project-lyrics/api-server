package com.projectlyrics.server.domain.auth.repository;

import com.projectlyrics.server.domain.auth.domain.Auth;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AuthRepository extends CrudRepository<Auth, String> {

    Optional<Auth> findByRefreshToken(String refreshToken);
    Optional<Auth> findByDeviceId(String deviceId);
}
