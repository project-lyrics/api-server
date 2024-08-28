package com.projectlyrics.server.domain.auth.authentication.jwt;

import com.projectlyrics.server.domain.user.entity.Role;

public record JwtClaim(long id, String nickname, Role role) {

    public static final String USER_ID = "userId";
    public static final String NICKNAME = "nickname";
    public static final String ROLE = "role";
}