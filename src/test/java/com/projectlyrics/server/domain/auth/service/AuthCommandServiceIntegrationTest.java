package com.projectlyrics.server.domain.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.projectlyrics.server.common.IntegrationTest;
import com.projectlyrics.server.common.fixture.UserFixture;
import com.projectlyrics.server.domain.auth.dto.request.AuthUserLoginRequest;
import com.projectlyrics.server.domain.auth.dto.response.AuthLoginResponse;
import com.projectlyrics.server.domain.auth.entity.enumerate.AuthProvider;
import com.projectlyrics.server.domain.auth.entity.enumerate.Role;
import com.projectlyrics.server.domain.auth.jwt.JwtTokenProvider;
import com.projectlyrics.server.domain.auth.service.social.kakao.KakaoSocialDataApiClient;
import com.projectlyrics.server.domain.auth.service.social.kakao.dto.KakaoAccount;
import com.projectlyrics.server.domain.auth.service.social.kakao.dto.KakaoUserInfoResponse;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.repository.UserCommandRepository;
import com.projectlyrics.server.domain.user.repository.UserQueryRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

public class AuthCommandServiceIntegrationTest extends IntegrationTest {

    @Autowired
    AuthCommandService sut;

    @Autowired
    UserCommandRepository userCommandRepository;

    @Autowired
    UserQueryRepository userQueryRepository;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @MockBean
    KakaoSocialDataApiClient kakaoSocialDataApiClient;

    @Test
    void 카카오_계정으로_로그인_해야_한다() throws Exception {
        //given
        String accessToken = "accessToken";
        User savedUser = userCommandRepository.save(UserFixture.createKakao());
        given(kakaoSocialDataApiClient.getUserInfo(any()))
                .willReturn(new KakaoUserInfoResponse(savedUser.getAuth().getSocialId(), new KakaoAccount(savedUser.getEmail())));

        //when
        AuthLoginResponse response = sut.signIn(new AuthUserLoginRequest(savedUser.getAuth().getSocialId(), AuthProvider.KAKAO));

        //then
        Long userId = jwtTokenProvider.getUserIdFromJwt(response.accessToken());
        assertThat(userId).isEqualTo(savedUser.getId());
    }

    //TODO apple 로그인 기능 추가 후 테스트
    @Test
    @Disabled("apple 로그인 기능 추가 후 테스트")
    void 애플_계정으로_로그인_해야_한다() throws Exception {
        //given
        String accessToken = "accessToken";
        User savedUser = userCommandRepository.save(UserFixture.createApple());
        given(kakaoSocialDataApiClient.getUserInfo(any()))
                .willReturn(new KakaoUserInfoResponse(savedUser.getAuth().getSocialId(), new KakaoAccount(savedUser.getEmail())));

        //when
        AuthLoginResponse response = sut.signIn(new AuthUserLoginRequest(accessToken, AuthProvider.APPLE));

        //then
        Long userId = jwtTokenProvider.getUserIdFromJwt(response.accessToken());
        assertThat(userId).isEqualTo(savedUser.getId());
    }
}
