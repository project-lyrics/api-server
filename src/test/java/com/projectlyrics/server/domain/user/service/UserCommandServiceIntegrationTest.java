package com.projectlyrics.server.domain.user.service;

import com.projectlyrics.server.common.IntegrationTest;
import com.projectlyrics.server.common.fixture.UserFixture;
import com.projectlyrics.server.domain.auth.dto.request.UserLoginRequest;
import com.projectlyrics.server.domain.auth.entity.enumerate.AuthProvider;
import com.projectlyrics.server.domain.auth.jwt.JwtTokenProvider;
import com.projectlyrics.server.domain.auth.service.kakao.KakaoSocialDataApiClient;
import com.projectlyrics.server.domain.auth.service.kakao.dto.KakaoAccount;
import com.projectlyrics.server.domain.auth.service.kakao.dto.KakaoUserInfoResponse;
import com.projectlyrics.server.domain.user.dto.response.UserLoginResponse;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.repository.UserCommandRepository;
import com.projectlyrics.server.domain.user.repository.UserQueryRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

class UserCommandServiceIntegrationTest extends IntegrationTest {

  @Autowired
  UserCommandService sut;

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
    UserLoginResponse response = sut.signIn(accessToken, new UserLoginRequest(AuthProvider.KAKAO));

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
    UserLoginResponse response = sut.signIn(accessToken, new UserLoginRequest(AuthProvider.KAKAO));

    //then
    Long userId = jwtTokenProvider.getUserIdFromJwt(response.accessToken());
    assertThat(userId).isEqualTo(savedUser.getId());
  }
}
