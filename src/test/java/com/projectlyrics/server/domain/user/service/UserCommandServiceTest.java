package com.projectlyrics.server.domain.user.service;

import com.projectlyrics.server.domain.user.dto.request.UserUpdateRequest;
import com.projectlyrics.server.domain.user.entity.ProfileCharacter;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.repository.UserCommandRepository;
import com.projectlyrics.server.domain.user.repository.UserQueryRepository;
import com.projectlyrics.server.support.IntegrationTest;
import com.projectlyrics.server.support.fixture.UserFixture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class UserCommandServiceTest extends IntegrationTest {

    @Autowired
    UserCommandRepository userCommandRepository;

    @Autowired
    UserQueryRepository userQueryRepository;

    @Autowired
    UserCommandService userCommandService;

    @Test
    void 사용자의_닉네임을_수정할_수_있다() {
        // given
        String newNickname = "pepper";
        UserUpdateRequest request = new UserUpdateRequest(newNickname, null);
        User user = userCommandRepository.save(UserFixture.create());

        // when
        userCommandService.update(request, user.getId());
        User result = userQueryRepository.findById(user.getId()).get();

        // then
        assertThat(result.getNickname().getValue()).isEqualTo(newNickname);
    }

    @Test
    void 사용자의_프로필을_수정할_수_있다() {
        // given
        ProfileCharacter newProfileCharacter = ProfileCharacter.PARTED_HAIR;
        UserUpdateRequest request = new UserUpdateRequest(null, newProfileCharacter);
        User user = userCommandRepository.save(UserFixture.create());

        // when
        userCommandService.update(request, user.getId());
        User result = userQueryRepository.findById(user.getId()).get();

        // then
        assertThat(result.getProfileCharacter()).isEqualTo(newProfileCharacter);
    }

    @Test
    void 요청이_null이면_수정하지_않는다() {
        // given
        UserUpdateRequest request = new UserUpdateRequest(null, null);
        User user = userCommandRepository.save(UserFixture.create());

        // when
        userCommandService.update(request, user.getId());
        User result = userQueryRepository.findById(user.getId()).get();

        // then
        assertAll(
                () -> assertThat(result.getNickname()).isEqualTo(user.getNickname()),
                () -> assertThat(result.getProfileCharacter()).isEqualTo(user.getProfileCharacter())
        );
    }
}