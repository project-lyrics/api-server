package com.projectlyrics.server.domain.user.service;

import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.domain.user.dto.request.UserUpdateRequest;
import com.projectlyrics.server.domain.user.entity.Gender;
import com.projectlyrics.server.domain.user.entity.ProfileCharacter;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.exception.FailedToUpdateProfileException;
import com.projectlyrics.server.domain.user.repository.UserCommandRepository;
import com.projectlyrics.server.domain.user.repository.UserQueryRepository;
import com.projectlyrics.server.support.IntegrationTest;
import com.projectlyrics.server.support.fixture.UserFixture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
        UserUpdateRequest request = new UserUpdateRequest(newNickname, null, null, null);
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
        UserUpdateRequest request = new UserUpdateRequest(null, newProfileCharacter, null, null);
        User user = userCommandRepository.save(UserFixture.create());

        // when
        userCommandService.update(request, user.getId());
        User result = userQueryRepository.findById(user.getId()).get();

        // then
        assertThat(result.getProfileCharacter()).isEqualTo(newProfileCharacter);
    }

    @Test
    void 사용자의_성별을_수정할_수_있다() {
        // given
        Gender newGender = Gender.FEMALE;
        UserUpdateRequest request = new UserUpdateRequest(null, null, newGender, null);
        User user = userCommandRepository.save(UserFixture.create());

        // when
        userCommandService.update(request, user.getId());
        User result = userQueryRepository.findById(user.getId()).get();

        // then
        assertThat(result.getInfo().getGender()).isEqualTo(newGender);
    }

    @Test
    void 사용자의_생년을_수정할_수_있다() {
        // given
        int newBirthYear = 1988;
        UserUpdateRequest request = new UserUpdateRequest(null, null, null, newBirthYear);
        User user = userCommandRepository.save(UserFixture.create());

        // when
        userCommandService.update(request, user.getId());
        User result = userQueryRepository.findById(user.getId()).get();

        // then
        assertThat(result.getInfo().getBirthYear()).isEqualTo(newBirthYear);
    }

    @Test
    void 요청의_두_항목이_모두_null이면_수정시에_예외가_발생한다() {
        // given
        UserUpdateRequest request = new UserUpdateRequest(null, null, null, null);
        User user = userCommandRepository.save(UserFixture.create());

        // when
        assertThatThrownBy(() -> userCommandService.update(request, user.getId()))
                .isInstanceOf(FailedToUpdateProfileException.class)
                .hasMessage(ErrorCode.FAILED_TO_UPDATE_PROFILE.getErrorMessage());
    }
}