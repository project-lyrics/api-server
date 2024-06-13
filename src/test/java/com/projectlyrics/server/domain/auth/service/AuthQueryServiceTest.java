package com.projectlyrics.server.domain.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.projectlyrics.server.common.IntegrationTest;
import com.projectlyrics.server.common.fixture.UserFixture;
import com.projectlyrics.server.domain.auth.dto.request.UserLoginRequest;
import com.projectlyrics.server.domain.auth.entity.enumerate.AuthProvider;
import com.projectlyrics.server.domain.auth.entity.enumerate.Role;
import com.projectlyrics.server.domain.auth.service.dto.AuthSocialInfo;
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

public class AuthQueryServiceTest extends IntegrationTest {

  @Autowired
  AuthQueryService sut;

  @Autowired
  UserQueryRepository userQueryRepository;

  @Autowired
  UserCommandRepository userCommandRepository;

  @MockBean
  KakaoSocialDataApiClient kakaoSocialDataApiClient;

  @Test
  void 카카오_유저_정보를_조회해야_한다() throws Exception {
    //given
    String accessToken = "accessToken";
    String socialId = "socialId";
    String email = "email";
    UserLoginRequest request = new UserLoginRequest(AuthProvider.KAKAO, Role.USER);
    given(kakaoSocialDataApiClient.getUserInfo(any()))
        .willReturn(new KakaoUserInfoResponse(socialId, new KakaoAccount(email)));

    //when
    AuthSocialInfo authSocialInfo = sut.getAuthSocialInfo(accessToken, request.authProvider());

    //then
    assertSoftly(s -> {
      s.assertThat(authSocialInfo.email()).isEqualTo(email);
      s.assertThat(authSocialInfo.socialId()).isEqualTo(socialId);
      s.assertThat(authSocialInfo.authProvider()).isEqualTo(AuthProvider.KAKAO);
    });
  }

  //TODO apple 로그인 기능 추가 후 테스트 새로 작성해야 함
  @Test
  @Disabled("apple 로그인 기능 추가 후 테스트")
  void 애플_유저_정보를_조회해야_한다() throws Exception {
    //given
    UserLoginRequest request = new UserLoginRequest(AuthProvider.APPLE, Role.USER);

    //when
    AuthSocialInfo authSocialInfo = sut.getAuthSocialInfo("access token", request.authProvider());

    //then
    assertThat(authSocialInfo).isNull();
  }
}
