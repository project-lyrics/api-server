package com.projectlyrics.server.domain.user.entity;

import com.projectlyrics.server.domain.auth.dto.request.AuthSignUpRequest;
import com.projectlyrics.server.domain.auth.entity.enumerate.AuthProvider;
import com.projectlyrics.server.domain.auth.exception.NotAgreeToTermsException;
import com.projectlyrics.server.domain.auth.service.dto.AuthSocialInfo;
import com.projectlyrics.server.domain.user.exception.InvalidAgeException;
import com.projectlyrics.server.domain.user.exception.InvalidUsernameException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Year;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void 유저를_생성해야_한다() throws Exception {
        //given

        //when then
        assertThatNoException().isThrownBy(() -> User.createUser(new AuthSocialInfo(AuthProvider.KAKAO, "socialId"),
                new AuthSignUpRequest(
                        "socialAccessToken",
                        AuthProvider.KAKAO,
                        "username",
                        Gender.MALE,
                        Year.of(1999),
                        List.of(new AuthSignUpRequest.TermsInput(true, "title", "agreement"))
                )));
    }

    @Test
    void 유저를_생성할_때_유저이름이_올바르지_않으면_예외가_발생해야_한다() throws Exception {
        //given
        String tooLongUsername = "usernameasdfsdfasdf";

        //when then
        assertThatThrownBy(() -> User.createUser(new AuthSocialInfo(AuthProvider.KAKAO, "socialId"),
                new AuthSignUpRequest(
                        "socialAccessToken",
                        AuthProvider.KAKAO,
                        tooLongUsername,
                        Gender.MALE,
                        Year.of(1999),
                        List.of(new AuthSignUpRequest.TermsInput(true, "title", "agreement"))
                )))
                .isInstanceOf(InvalidUsernameException.class);
    }

    @Test
    void 유저를_생성할_때_약관동의하지_않으면_예외가_발생해야_한다() throws Exception {
        //given
        List<AuthSignUpRequest.TermsInput> agreement = List.of(new AuthSignUpRequest.TermsInput(false, "title", "agreement"));

        //when then
        assertThatThrownBy(() -> User.createUser(new AuthSocialInfo(AuthProvider.KAKAO, "socialId"),
                new AuthSignUpRequest(
                        "socialAccessToken",
                        AuthProvider.KAKAO,
                        "username",
                        Gender.MALE,
                        Year.of(1999),
                        agreement
                )))
                .isInstanceOf(NotAgreeToTermsException.class);
    }

    @Test
    void 유저를_생성할_때_만14세_아래면_예외가_발생해야_한다() throws Exception {
        //given
        Year year = Year.now().minusYears(13);
        
        //when then
        assertThatThrownBy(() -> User.createUser(new AuthSocialInfo(AuthProvider.KAKAO, "socialId"),
                new AuthSignUpRequest(
                        "socialAccessToken",
                        AuthProvider.KAKAO,
                        "username",
                        Gender.MALE,
                        year,
                        List.of(new AuthSignUpRequest.TermsInput(true, "title", "agreement"))
                )))
                .isInstanceOf(InvalidAgeException.class);
    }
}
