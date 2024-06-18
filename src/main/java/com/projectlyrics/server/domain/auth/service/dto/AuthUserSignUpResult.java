package com.projectlyrics.server.domain.auth.service.dto;

import com.projectlyrics.server.domain.user.entity.User;

public record AuthUserSignUpResult(
        User user,
        boolean isRegistered
) {

}
