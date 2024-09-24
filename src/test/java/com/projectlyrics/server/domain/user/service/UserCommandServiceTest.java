package com.projectlyrics.server.domain.user.service;

import com.projectlyrics.server.domain.user.controller.dto.request.UserUpdateRequest;
import com.projectlyrics.server.domain.user.entity.Gender;
import com.projectlyrics.server.domain.user.entity.ProfileCharacter;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.entity.UserMetaInfo;
import com.projectlyrics.server.domain.user.entity.usecase.UserUpdateType;
import com.projectlyrics.server.domain.user.repository.UserCommandRepository;
import com.projectlyrics.server.domain.user.repository.UserQueryRepository;
import com.projectlyrics.server.support.IntegrationTest;
import com.projectlyrics.server.support.fixture.UserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class UserCommandServiceTest extends IntegrationTest {

    @Autowired
    UserCommandRepository userCommandRepository;

    @Autowired
    UserQueryRepository userQueryRepository;

    @Autowired
    UserCommandService sut;

    User user;

    @BeforeEach
    void setUp() {
        user = userCommandRepository.save(UserFixture.create());
    }

    @Test
    void 사용자의_닉네임을_수정할_수_있다() {
        // given
        String nickname = "konu";
        UserUpdateRequest request = new UserUpdateRequest(
                UserUpdateType.NICKNAME,
                nickname,
                null,
                null,
                null
        );

        // when
        sut.update(request, user.getId());

        // then
        User result = userQueryRepository.findById(user.getId()).get();

        assertThat(result.getNickname().getValue()).isEqualTo(nickname);
    }

    @Test
    void 사용자의_프로필_캐릭터를_수정할_수_있다() {
        // given
        ProfileCharacter profileCharacter = ProfileCharacter.BRAIDED_HAIR;
        UserUpdateRequest request = new UserUpdateRequest(
                UserUpdateType.PROFILE_CHARACTER,
                null,
                profileCharacter,
                null,
                null
        );

        // when
        sut.update(request, user.getId());

        // then
        User result = userQueryRepository.findById(user.getId()).get();

        assertThat(result.getProfileCharacter()).isEqualTo(profileCharacter);
    }

    @Test
    void 사용자의_메타_정보를_수정할_수_있다() {
        // given
        UserMetaInfo metaInfo = new UserMetaInfo(Gender.FEMALE, 1979);
        UserUpdateRequest request = new UserUpdateRequest(
                UserUpdateType.META_INFO,
                null,
                null,
                metaInfo.getGender(),
                metaInfo.getBirthYear()
        );

        // when
        sut.update(request, user.getId());

        // then
        User result = userQueryRepository.findById(user.getId()).get();

        assertThat(result.getInfo().getGender()).isEqualTo(metaInfo.getGender());
        assertThat(result.getInfo().getBirthYear()).isEqualTo(metaInfo.getBirthYear());
    }

    @Test
    void 사용자_수정시_타입과_관련되지_않은_정보는_수정하지_않는다() {
        // given
        String nickname = "konu";
        UserUpdateRequest request = new UserUpdateRequest(
                UserUpdateType.NICKNAME,
                nickname,
                ProfileCharacter.PARTED_HAIR,
                Gender.FEMALE,
                1975
        );

        // when
        sut.update(request, user.getId());

        // then
        User result = userQueryRepository.findById(user.getId()).get();

        assertThat(result.getProfileCharacter()).isNotEqualTo(request.profileCharacter());
        assertThat(result.getInfo().getGender()).isNotEqualTo(request.gender());
        assertThat(result.getInfo().getBirthYear()).isNotEqualTo(request.birthYear());
    }
}