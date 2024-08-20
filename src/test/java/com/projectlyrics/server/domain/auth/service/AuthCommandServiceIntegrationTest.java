package com.projectlyrics.server.domain.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

import com.projectlyrics.server.domain.auth.exception.AlreadyExistsUserException;
import com.projectlyrics.server.domain.auth.service.social.apple.dto.AppleUserInfo;
import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.domain.user.entity.*;
import com.projectlyrics.server.support.IntegrationTest;
import com.projectlyrics.server.support.fixture.UserFixture;
import com.projectlyrics.server.domain.auth.authentication.jwt.JwtExtractor;
import com.projectlyrics.server.domain.auth.authentication.jwt.JwtProvider;
import com.projectlyrics.server.domain.auth.dto.request.AuthSignInRequest;
import com.projectlyrics.server.domain.auth.dto.request.AuthSignUpRequest;
import com.projectlyrics.server.domain.auth.dto.response.AuthTokenResponse;
import com.projectlyrics.server.domain.auth.exception.NotAgreeToTermsException;
import com.projectlyrics.server.domain.auth.service.social.apple.AppleSocialService;
import com.projectlyrics.server.domain.auth.service.social.kakao.KakaoSocialDataApiClient;
import com.projectlyrics.server.domain.auth.service.social.kakao.dto.KakaoAccount;
import com.projectlyrics.server.domain.auth.service.social.kakao.dto.KakaoUserInfo;
import com.projectlyrics.server.domain.user.exception.UserNotFoundException;
import com.projectlyrics.server.domain.user.repository.UserCommandRepository;
import com.projectlyrics.server.domain.user.repository.UserQueryRepository;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.time.Year;
import java.util.List;

public class AuthCommandServiceIntegrationTest extends IntegrationTest {

    @Autowired
    AuthCommandService sut;

    @Autowired
    UserCommandRepository userCommandRepository;

    @Autowired
    UserQueryRepository userQueryRepository;

    @Autowired
    AuthQueryService authQueryService;

    @Autowired
    JwtProvider jwtProvider;

    @Autowired
    JwtExtractor jwtExtractor;

    @SpyBean
    KakaoSocialDataApiClient kakaoSocialDataApiClient;

    @SpyBean
    AppleSocialService appleSocialService;

    @Test
    void 카카오_계정으로_로그인_해야_한다() throws Exception {
        //given
        String socialAccessToken = "accessToken";
        User savedUser = userCommandRepository.save(UserFixture.builder().kakao().build());
        doReturn(new KakaoUserInfo(savedUser.getSocialInfo().getSocialId(), new KakaoAccount()))
                .when(kakaoSocialDataApiClient).getUserInfo(any());

        //when
        AuthTokenResponse response = sut.signIn(new AuthSignInRequest(socialAccessToken, AuthProvider.KAKAO));

        //then
        Long userId = jwtExtractor.parseJwtClaim(response.accessToken()).id();
        assertThat(userId).isEqualTo(savedUser.getId());
    }

    @Test
    void 없는_유저인_경우_로그인에_실패해야_한다() throws Exception {
        //given
        String socialAccessToken = "accessToken";
        doReturn(new KakaoUserInfo("socialId", new KakaoAccount()))
                .when(kakaoSocialDataApiClient).getUserInfo(any());

        //when then
        assertThatThrownBy(() -> sut.signIn(new AuthSignInRequest(socialAccessToken, AuthProvider.KAKAO)))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void 소셜_인증에_실패한_경우_로그인에_실패해야_한다() throws Exception {
        //given
        String socialAccessToken = "accessToken";

        //when then
        assertThatThrownBy(() -> sut.signIn(new AuthSignInRequest(socialAccessToken, AuthProvider.KAKAO)))
                .isInstanceOf(FeignException.class);
    }

    @Test
    void 애플_계정으로_로그인_해야_한다() throws Exception {
        //given
        String accessToken = "accessToken";
        User savedUser = userCommandRepository.save(UserFixture.builder().apple().build());
        doReturn(SocialInfo.from(new AppleUserInfo(savedUser.getSocialInfo().getSocialId())))
                .when(appleSocialService).getSocialData(any());

        //when
        AuthTokenResponse response = sut.signIn(new AuthSignInRequest(accessToken, AuthProvider.APPLE));

        //then
        Long userId = jwtExtractor.parseJwtClaim(response.accessToken()).id();
        assertThat(userId).isEqualTo(savedUser.getId());
    }

    @Test
    void 회원가입_해야_한다() throws Exception {
        //given
        User user = UserFixture.create();
        AuthSignUpRequest request = new AuthSignUpRequest(
                "socialAccessToken",
                AuthProvider.KAKAO,
                "nickname",
                ProfileCharacter.POOP_HAIR,
                Gender.MALE,
                Year.of(1999),
                List.of(new AuthSignUpRequest.TermsInput(true, "title", "agreement"))
        );
        doReturn(new KakaoUserInfo(user.getSocialInfo().getSocialId(), new KakaoAccount()))
                .when(kakaoSocialDataApiClient).getUserInfo(any());

        //when
        AuthTokenResponse response = sut.signUp(request);

        //then
        User findUser = userQueryRepository.findBySocialIdAndAuthProvider(user.getSocialInfo().getSocialId(), AuthProvider.KAKAO).get();
        Long userId = jwtExtractor.parseJwtClaim(response.accessToken()).id();
        assertThat(userId).isEqualTo(findUser.getId());
    }

    @Test
    void 회원가입할_때_약관에_동의하지_않은_경우_예외가_발생해야_한다() throws Exception {
        //given
        User user = UserFixture.create();
        AuthSignUpRequest request = new AuthSignUpRequest(
                "socialAccessToken",
                AuthProvider.KAKAO,
                "nickname",
                ProfileCharacter.POOP_HAIR,
                Gender.MALE,
                Year.of(1999),
                List.of(new AuthSignUpRequest.TermsInput(false, "title", "agreement"))
        );
        doReturn(new KakaoUserInfo(user.getSocialInfo().getSocialId(), new KakaoAccount()))
                .when(kakaoSocialDataApiClient).getUserInfo(any());

        //when then
        assertThatThrownBy(() -> sut.signUp(request))
                .isInstanceOf(NotAgreeToTermsException.class);
    }

    @Test
    void 소셜_인증에_실패한_경우_회원가입에_실패해야_한다() throws Exception {
        //given
        AuthSignUpRequest request = new AuthSignUpRequest(
                "socialAccessToken",
                AuthProvider.KAKAO,
                "nickname",
                ProfileCharacter.POOP_HAIR,
                Gender.MALE,
                Year.of(1999),
                List.of(new AuthSignUpRequest.TermsInput(true, "title", "agreement"))
        );

        //when then
        assertThatThrownBy(() -> sut.signUp(request))
                .isInstanceOf(FeignException.class);
    }

    @Test
    void 이미_있는_유저인_경우_회원가입에_실패_해야_한다() throws Exception {
        //given
        User user = userCommandRepository.save(UserFixture.create());
        AuthSignUpRequest request = new AuthSignUpRequest(
                "socialAccessToken",
                AuthProvider.KAKAO,
                "nickname",
                ProfileCharacter.POOP_HAIR,
                Gender.MALE,
                Year.of(1999),
                List.of(new AuthSignUpRequest.TermsInput(true, "title", "agreement"))
        );
        doReturn(new KakaoUserInfo(user.getSocialInfo().getSocialId(), new KakaoAccount()))
                .when(kakaoSocialDataApiClient).getUserInfo(any());

        //when then
        assertThatThrownBy(() -> sut.signUp(request))
                .isInstanceOf(AlreadyExistsUserException.class)
                .hasMessage(ErrorCode.USER_ALREADY_EXISTS.getErrorMessage());
    }
}
