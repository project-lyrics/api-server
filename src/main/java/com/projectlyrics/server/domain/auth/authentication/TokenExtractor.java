package com.projectlyrics.server.domain.auth.authentication;

import com.projectlyrics.server.domain.auth.exception.InvalidTokenPrefixException;
import com.projectlyrics.server.domain.auth.exception.NoTokenProvidedException;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class TokenExtractor {

    public static final String BEARER = "Bearer ";

    public String extractToken(String authorization) {
        if (Objects.isNull(authorization)) {
            throw new NoTokenProvidedException();
        }
        if (!authorization.startsWith(BEARER)) {
            throw new InvalidTokenPrefixException();
        }
        return authorization.substring(BEARER.length());
    }
}
