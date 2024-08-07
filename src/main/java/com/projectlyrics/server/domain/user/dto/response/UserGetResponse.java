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
                user.getNickname().getValue(),
                user.getProfileCharacter().getType()
        );
    }
}
