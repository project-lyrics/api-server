package com.projectlyrics.server.global.auth.jwt.redis.repository;

import com.projectlyrics.server.global.auth.jwt.redis.domain.Token;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface TokenRepository extends CrudRepository<Token, Long> {

    Optional<Token> findByRefreshToken(String refreshToken);
}