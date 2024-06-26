package com.projectlyrics.server.domain.user.entity;

import com.projectlyrics.server.domain.auth.dto.request.AuthSignUpRequest;
import com.projectlyrics.server.domain.auth.entity.enumerate.AuthProvider;
import com.projectlyrics.server.domain.auth.service.dto.AuthSocialInfo;
import com.projectlyrics.server.domain.user.exception.InvalidUsernameException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Year;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void 유저를_생성해야_한다() throws Exception {
        //given

        //when then
        assertThatNoException().isThrownBy(() -> User.createUser(new AuthSocialInfo(AuthProvider.KAKAO, "socialId", "email"),
                new AuthSignUpRequest(
                        "socialAccessToken",
                        AuthProvider.KAKAO,
                        "username",
                        Gender.MALE,
                        Year.of(1999),
                        new AuthSignUpRequest.TermsInput(true, "agreement")
                )));
    }

    @Test
    void 유저를_생성할_때_유저이름이_올바르지_않으면_예외가_발생해야_한다() throws Exception {
        //given
        String tooLongUsername = "usernameasdfsdfasdf";

        //when then
        assertThatThrownBy(() -> User.createUser(new AuthSocialInfo(AuthProvider.KAKAO, "socialId", "email"),
                new AuthSignUpRequest(
                        "socialAccessToken",
                        AuthProvider.KAKAO,
                        tooLongUsername,
                        Gender.MALE,
                        Year.of(1999),
                        new AuthSignUpRequest.TermsInput(true, "agreement")
                )))
                .isInstanceOf(InvalidUsernameException.class);
    }
}
