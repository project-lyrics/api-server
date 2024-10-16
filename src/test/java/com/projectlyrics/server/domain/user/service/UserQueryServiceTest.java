package com.projectlyrics.server.domain.user.service;

import com.projectlyrics.server.domain.user.dto.response.UserProfileResponse;
import com.projectlyrics.server.domain.user.entity.*;
import com.projectlyrics.server.domain.user.repository.UserCommandRepository;
import com.projectlyrics.server.domain.user.repository.UserQueryRepository;
import com.projectlyrics.server.support.IntegrationTest;
import com.projectlyrics.server.support.fixture.UserFixture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class UserQueryServiceTest extends IntegrationTest {

    @Autowired
    UserCommandRepository userCommandRepository;

    @Autowired
    UserQueryRepository userQueryRepository;

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

    @Test
    void 성별과_출생연도가_null인_사용자의_프로필을_조회할_수_있다() {
        // given
        User user = userCommandRepository.save(User.withId(
                1L,
                SocialInfo.of(AuthProvider.KAKAO, "socialId"),
                "nickname",
                ProfileCharacter.PARTED_HAIR,
                Role.USER,
                null,
                null,
                List.of(new TermsAgreements(true, "title", "agreement"))
        ));

        // when
        UserProfileResponse result = sut.getById(user.getId());

        // then
        assertAll(
                () -> assertThat(result.gender()).isNull(),
                () -> assertThat(result.birthYear()).isNull()
        );
    }
}