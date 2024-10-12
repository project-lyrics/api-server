package com.projectlyrics.server.domain.user.dto.response;

import com.projectlyrics.server.domain.user.entity.AuthProvider;
import com.projectlyrics.server.domain.user.entity.Gender;
import com.projectlyrics.server.domain.user.entity.User;

public record UserProfileResponse(
        Long id,
        String nickname,
        String profileCharacterType,
        Gender gender,
        Integer birthYear,
        String feedbackId,
        AuthProvider authProvider
) {

    public static UserProfileResponse from(User user) {
        return new UserProfileResponse(
                user.getId(),
                user.getNickname().getValue(),
                user.getProfileCharacter().getType(),
                user.getInfo().getGender(),
                user.getInfo().getBirthYear(),
                user.getFeedbackId(),
                user.getSocialInfo().getAuthProvider()
        );
    }
}
