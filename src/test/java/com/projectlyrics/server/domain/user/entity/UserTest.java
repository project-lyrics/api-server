package com.projectlyrics.server.domain.user.entity;

import com.projectlyrics.server.domain.auth.dto.request.AuthSignUpRequest;
import com.projectlyrics.server.domain.auth.exception.NotAgreeToTermsException;
import com.projectlyrics.server.domain.user.exception.InvalidAgeException;
import com.projectlyrics.server.domain.user.exception.InvalidUsernameException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Year;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

class UserTest {

    private SocialInfo socialInfo;
    private AuthSignUpRequest request;

    @BeforeEach
    void setUp() {
        socialInfo = SocialInfo.of(AuthProvider.KAKAO, "socialId");
        request = new AuthSignUpRequest(
                "socialAccessToken",
                AuthProvider.KAKAO,
                "nickname",
                ProfileCharacter.POOP_HAIR,
                Gender.MALE,
                Year.of(1999),
                List.of(new AuthSignUpRequest.TermsInput(true, "title", "agreement"))
        );
    }

    @Test
    void 유저를_생성해야_한다() throws Exception {
        // when
        User user = User.create(UserCreate.of(socialInfo, request));

        // then
        assertAll(
                () -> assertThat(user.getSocialInfo()).isEqualTo(socialInfo),
                () -> assertThat(user.getNickname().getValue()).isEqualTo(request.nickname()),
                () -> assertThat(user.getProfileCharacter()).isEqualTo(request.profileCharacter()),
                () -> assertThat(user.getInfo().getGender()).isEqualTo(request.gender()),
                () -> assertThat(user.getInfo().getBirthYear()).isEqualTo(request.birthYear().getValue()),
                () -> assertThat(user.getTermsAgreements()).hasSize(1)
        );
    }

    @Test
    void 유저를_생성할_때_유저이름이_올바르지_않으면_예외가_발생해야_한다() throws Exception {
        // given
        String tooLongUsername = "usernameasdfsdfasdf";

        // when, then
        assertThatThrownBy(() -> User.create(UserCreate.of(socialInfo,
                new AuthSignUpRequest(
                        "socialAccessToken",
                        AuthProvider.KAKAO,
                        tooLongUsername,
                        ProfileCharacter.POOP_HAIR,
                        Gender.MALE,
                        Year.of(1999),
                        List.of(new AuthSignUpRequest.TermsInput(true, "title", "agreement"))
                ))))
                .isInstanceOf(InvalidUsernameException.class);
    }

    @Test
    void 유저를_생성할_때_약관동의하지_않으면_예외가_발생해야_한다() throws Exception {
        // given
        List<AuthSignUpRequest.TermsInput> agreement = List.of(new AuthSignUpRequest.TermsInput(false, "title", "agreement"));

        // when, then
        assertThatThrownBy(() -> User.create(UserCreate.of(socialInfo,
                new AuthSignUpRequest(
                        "socialAccessToken",
                        AuthProvider.KAKAO,
                        "nickname",
                        ProfileCharacter.POOP_HAIR,
                        Gender.MALE,
                        Year.of(1999),
                        agreement
                ))))
                .isInstanceOf(NotAgreeToTermsException.class);
    }

    @Test
    void 유저를_생성할_때_만14세_아래면_예외가_발생해야_한다() throws Exception {
        //given
        Year year = Year.now().minusYears(13);
        
        //when then
        assertThatThrownBy(() -> User.create(UserCreate.of(socialInfo,
                new AuthSignUpRequest(
                        "socialAccessToken",
                        AuthProvider.KAKAO,
                        "nickname",
                        ProfileCharacter.POOP_HAIR,
                        Gender.MALE,
                        year,
                        List.of(new AuthSignUpRequest.TermsInput(true, "title", "agreement"))
                ))))
                .isInstanceOf(InvalidAgeException.class);
    }
}
