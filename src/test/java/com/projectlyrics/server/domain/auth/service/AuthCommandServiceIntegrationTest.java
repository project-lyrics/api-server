package com.projectlyrics.server.domain.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;

import com.projectlyrics.server.common.IntegrationTest;
import com.projectlyrics.server.common.fixture.UserFixture;
import com.projectlyrics.server.domain.auth.dto.request.AuthSignInRequest;
import com.projectlyrics.server.domain.auth.dto.request.AuthSignUpRequest;
import com.projectlyrics.server.domain.auth.dto.response.AuthTokenResponse;
import com.projectlyrics.server.domain.auth.entity.enumerate.AuthProvider;
import com.projectlyrics.server.domain.auth.exception.NotAgreeToTermsException;
import com.projectlyrics.server.domain.auth.jwt.JwtTokenProvider;
import com.projectlyrics.server.domain.auth.service.dto.AuthSocialInfo;
import com.projectlyrics.server.domain.auth.service.social.apple.ApplePublicKeysApiClient;
import com.projectlyrics.server.domain.auth.service.social.apple.AppleSocialService;
import com.projectlyrics.server.domain.auth.service.social.apple.dto.AppleUserInfoResponse;
import com.projectlyrics.server.domain.auth.service.social.kakao.KakaoSocialDataApiClient;
import com.projectlyrics.server.domain.auth.service.social.kakao.dto.KakaoAccount;
import com.projectlyrics.server.domain.auth.service.social.kakao.dto.KakaoUserInfoResponse;
import com.projectlyrics.server.domain.user.entity.Gender;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.exception.UserNotFoundException;
import com.projectlyrics.server.domain.user.repository.UserCommandRepository;
import com.projectlyrics.server.domain.user.repository.UserQueryRepository;
import feign.FeignException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.time.Year;
import java.util.List;
import java.util.Optional;

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
    JwtTokenProvider jwtTokenProvider;

    @SpyBean
    KakaoSocialDataApiClient kakaoSocialDataApiClient;

    @SpyBean
    AppleSocialService appleSocialService;

    @Test
    void 카카오_계정으로_로그인_해야_한다() throws Exception {
        //given
        String socialAccessToken = "accessToken";
        User savedUser = userCommandRepository.save(UserFixture.builder().kakao().build());
        doReturn(new KakaoUserInfoResponse(savedUser.getAuth().getSocialId(), new KakaoAccount()))
                .when(kakaoSocialDataApiClient).getUserInfo(any());

        //when
        AuthTokenResponse response = sut.signIn(new AuthSignInRequest(socialAccessToken, AuthProvider.KAKAO));

        //then
        Long userId = jwtTokenProvider.getUserIdFromJwt(response.accessToken());
        assertThat(userId).isEqualTo(savedUser.getId());
    }

    @Test
    void 없는_유저인_경우_로그인에_실패해야_한다() throws Exception {
        //given
        String socialAccessToken = "accessToken";
        doReturn(new KakaoUserInfoResponse("socialId", new KakaoAccount()))
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
        doReturn(new AuthSocialInfo(AuthProvider.APPLE, savedUser.getAuth().getSocialId()))
                .when(appleSocialService).getSocialData(any());

        //when
        AuthTokenResponse response = sut.signIn(new AuthSignInRequest(accessToken, AuthProvider.APPLE));

        //then
        Long userId = jwtTokenProvider.getUserIdFromJwt(response.accessToken());
        assertThat(userId).isEqualTo(savedUser.getId());
    }

    @Test
    void 회원가입_해야_한다() throws Exception {
        //given
        User user = UserFixture.create();
        AuthSignUpRequest request = new AuthSignUpRequest(
                "socialAccessToken",
                AuthProvider.KAKAO,
                "username",
                Gender.MALE,
                Year.of(1999),
                List.of(new AuthSignUpRequest.TermsInput(true, "title", "agreement"))
        );
        doReturn(new KakaoUserInfoResponse(user.getAuth().getSocialId(), new KakaoAccount()))
                .when(kakaoSocialDataApiClient).getUserInfo(any());

        //when
        AuthTokenResponse response = sut.signUp(request);

        //then
        User findUser = userQueryRepository.findBySocialIdAndAuthProviderAndNotDeleted(user.getAuth().getSocialId(), AuthProvider.KAKAO).get();
        Long userId = jwtTokenProvider.getUserIdFromJwt(response.accessToken());
        assertThat(userId).isEqualTo(findUser.getId());
    }

    @Test
    void 회원가입할_때_약관에_동의하지_않은_경우_예외가_발생해야_한다() throws Exception {
        //given
        User user = UserFixture.create();
        AuthSignUpRequest request = new AuthSignUpRequest(
                "socialAccessToken",
                AuthProvider.KAKAO,
                "username",
                Gender.MALE,
                Year.of(1999),
                List.of(new AuthSignUpRequest.TermsInput(false, "title", "agreement"))
        );
        doReturn(new KakaoUserInfoResponse(user.getAuth().getSocialId(), new KakaoAccount()))
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
                "username",
                Gender.MALE,
                Year.of(1999),
                List.of(new AuthSignUpRequest.TermsInput(true, "title", "agreement"))
        );

        //when then
        assertThatThrownBy(() -> sut.signUp(request))
                .isInstanceOf(FeignException.class);
    }
}
