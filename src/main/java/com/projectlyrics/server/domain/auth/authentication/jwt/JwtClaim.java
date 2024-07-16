package com.projectlyrics.server.domain.auth.authentication.jwt;

public record JwtClaim(long id, String nickname) {

    public static final String USER_ID = "userId";
    public static final String NICKNAME = "nickname";
}