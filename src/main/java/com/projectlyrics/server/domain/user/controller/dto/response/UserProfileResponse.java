package com.projectlyrics.server.domain.user.controller.dto.response;

import com.projectlyrics.server.domain.user.entity.AuthProvider;
import com.projectlyrics.server.domain.user.entity.ProfileCharacter;
import com.projectlyrics.server.domain.user.entity.UserMetaInfo;

public record UserProfileResponse(
        Long id,
        String nickname,
        ProfileCharacter profileCharacter,
        AuthProvider authProvider,
        String feedbackId,
        UserMetaInfo metaInfo
) {
}
