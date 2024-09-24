package com.projectlyrics.server.domain.user.service;

import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.domain.user.controller.dto.response.UserProfileResponse;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.exception.UserNotFoundException;
import com.projectlyrics.server.domain.user.repository.UserCommandRepository;
import com.projectlyrics.server.support.IntegrationTest;
import com.projectlyrics.server.support.fixture.UserFixture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class UserQueryServiceTest extends IntegrationTest {

    @Autowired
    UserCommandRepository userCommandRepository;

    @Autowired
    UserQueryService sut;

    @Test
    void 사용자_id를_통해_사용자의_프로필을_조회할_수_있다() {
        // given
        User user = userCommandRepository.save(UserFixture.create());

        // when
        UserProfileResponse result = sut.getProfile(user.getId());

        // then
        assertAll(
                () -> assertThat(result.id()).isEqualTo(user.getId()),
                () -> assertThat(result.nickname()).isEqualTo(user.getNickname().getValue()),
                () -> assertThat(result.profileCharacter()).isEqualTo(user.getProfileCharacter()),
                () -> assertThat(result.authProvider().name()).isEqualTo(user.getSocialInfo().getAuthProvider().name()),
                () -> assertThat(result.feedbackId()).isEqualTo(user.getFeedbackId()),
                () -> assertThat(result.metaInfo().getGender()).isEqualTo(user.getInfo().getGender()),
                () -> assertThat(result.metaInfo().getBirthYear().intValue()).isEqualTo(user.getInfo().getBirthYear().intValue())
        );
    }

    @Test
    void 존재하지_않는_사용자의_프로필을_조회할_경우_예외가_발생한다() {
        // when, then
        assertThatThrownBy(() -> sut.getProfile(1L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage(ErrorCode.USER_NOT_FOUND.getErrorMessage());
    }
}