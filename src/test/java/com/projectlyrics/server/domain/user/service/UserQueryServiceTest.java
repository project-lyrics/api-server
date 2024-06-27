package com.projectlyrics.server.domain.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.projectlyrics.server.common.IntegrationTest;
import com.projectlyrics.server.common.fixture.UserFixture;
import com.projectlyrics.server.domain.auth.entity.enumerate.AuthProvider;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.exception.UserNotFoundException;
import com.projectlyrics.server.domain.user.repository.UserCommandRepository;
import com.projectlyrics.server.domain.user.repository.UserQueryRepository;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class UserQueryServiceTest extends IntegrationTest {

    @Autowired
    UserQueryService sut;

    @Autowired
    UserQueryRepository userQueryRepository;

    @Autowired
    UserCommandRepository userCommandRepository;

    @Test
    void id로_유저를_조회해야_한다() throws Exception {
        //given
        User savedUser = userCommandRepository.save(UserFixture.create());

        //when
        User user = sut.getUserById(savedUser.getId());

        //then
        assertThat(user).isEqualTo(savedUser);
    }

    @Test
    void 존재하지_않는_id로_유저를_조회하면_예외가_발생한다() throws Exception {
        //given, when, then
        assertThatThrownBy(() -> sut.getUserById(1L))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void 소셜_정보로_유저를_조회해야_한다() throws Exception {
        //given
        User savedUser = userCommandRepository.save(UserFixture.create());

        //when
        User user = sut.getUserBySocialInfo(savedUser.getAuth().getSocialId(), AuthProvider.KAKAO);

        //then
        assertThat(user).isEqualTo(savedUser);
    }

    @Test
    void 소셜_정보로_없는_유저를_조회하면_예외가_발생해야_한다() throws Exception {
        //given

        //when then
        assertThatThrownBy(() -> sut.getUserBySocialInfo("socialId", AuthProvider.KAKAO))
                .isInstanceOf(UserNotFoundException.class);
    }
}
