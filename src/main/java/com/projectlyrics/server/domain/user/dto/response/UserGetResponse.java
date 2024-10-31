package com.projectlyrics.server.domain.user.dto.response;

import com.projectlyrics.server.domain.user.entity.User;

public record UserGetResponse(
        Long id,
        String nickname,
        String profileCharacterType
) {

    public static UserGetResponse from(User user) {
        return new UserGetResponse(
                user.getId(),
                user.getDeletedAt() == null ? user.getNickname().getValue() : "(알 수 없음)",
                user.getDeletedAt() == null ? user.getProfileCharacter().getType() : "shortHair"
        );
    }
}
