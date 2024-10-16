package com.projectlyrics.server.domain.user.dto.response;

import com.projectlyrics.server.domain.user.entity.AuthProvider;
import com.projectlyrics.server.domain.user.entity.Gender;
import com.projectlyrics.server.domain.user.entity.User;

import java.util.Objects;

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
                Objects.isNull(user.getInfo()) ? null : user.getInfo().getGender(),
                Objects.isNull(user.getInfo()) ? null : user.getInfo().getBirthYear(),
                user.getFeedbackId(),
                user.getSocialInfo().getAuthProvider()
        );
    }
}
