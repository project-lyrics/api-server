package com.projectlyrics.server.domain.user.dto.request;

import com.projectlyrics.server.domain.user.entity.Gender;
import com.projectlyrics.server.domain.user.entity.ProfileCharacter;

public record UserUpdateRequest(
        String nickname,
        ProfileCharacter profileCharacter,
        Gender gender,
        Integer birthYear
) {
}
