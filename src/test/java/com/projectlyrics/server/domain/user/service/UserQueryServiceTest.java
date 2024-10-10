package com.projectlyrics.server.domain.user.service;

import com.projectlyrics.server.domain.user.dto.response.UserProfileResponse;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.repository.UserCommandRepository;
import com.projectlyrics.server.support.IntegrationTest;
import com.projectlyrics.server.support.fixture.UserFixture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class UserQueryServiceTest extends IntegrationTest {

    @Autowired
    UserCommandRepository userCommandRepository;

    @Autowired
    UserQueryService sut;

    @Test
    void 사용자의_프로필을_조회할_수_있다() {
        // given
        User user = userCommandRepository.save(UserFixture.create());

        // when
        UserProfileResponse result = sut.getById(user.getId());

        // then
        assertAll(
                () -> assertThat(result.id()).isEqualTo(user.getId()),
                () -> assertThat(result.nickname()).isEqualTo(user.getNickname().getValue()),
                () -> assertThat(result.profileCharacterType()).isEqualTo(user.getProfileCharacter().getType()),
                () -> assertThat(result.feedbackId()).isEqualTo(user.getFeedbackId()),
                () -> assertThat(result.authProvider()).isEqualTo(user.getSocialInfo().getAuthProvider())
        );
    }
}