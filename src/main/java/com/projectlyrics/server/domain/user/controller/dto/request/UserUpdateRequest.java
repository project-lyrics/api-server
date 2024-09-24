package com.projectlyrics.server.domain.user.controller.dto.request;

import com.projectlyrics.server.domain.user.entity.Gender;
import com.projectlyrics.server.domain.user.entity.ProfileCharacter;
import com.projectlyrics.server.domain.user.entity.usecase.UserUpdateType;

public record UserUpdateRequest(
        UserUpdateType type,
        String nickname,
        ProfileCharacter profileCharacter,
        Gender gender,
        Integer birthYear
) {
}
