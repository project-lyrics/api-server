package com.projectlyrics.server.domain.user.service;

import com.projectlyrics.server.common.IntegrationTest;
import com.projectlyrics.server.common.fixture.UserFixture;
import com.projectlyrics.server.domain.auth.dto.request.UserLoginRequest;
import com.projectlyrics.server.domain.auth.dto.response.UserInfoResponse;
import com.projectlyrics.server.domain.auth.entity.enumerate.AuthProvider;
import com.projectlyrics.server.domain.auth.jwt.JwtTokenProvider;
import com.projectlyrics.server.domain.auth.service.kakao.KakaoSocialDataApiClient;
import com.projectlyrics.server.domain.auth.service.kakao.dto.KakaoAccount;
import com.projectlyrics.server.domain.auth.service.kakao.dto.KakaoUserInfoResponse;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.repository.UserCommandRepository;
import com.projectlyrics.server.domain.user.repository.UserQueryRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

class UserQueryServiceTest extends IntegrationTest {

  @Autowired
  UserQueryService sut;

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
    UserLoginRequest request = new UserLoginRequest(AuthProvider.KAKAO);
    given(kakaoSocialDataApiClient.getUserInfo(any()))
        .willReturn(new KakaoUserInfoResponse(socialId, new KakaoAccount(email)));

    //when
    UserInfoResponse response = sut.getUserInfoByAccessToken(accessToken, request);

    //then
    assertSoftly(s -> {
      s.assertThat(response.email()).isEqualTo(email);
      s.assertThat(response.socialId()).isEqualTo(socialId);
      s.assertThat(response.authProvider()).isEqualTo(AuthProvider.KAKAO);
    });
  }

  //TODO apple 로그인 기능 추가 후 테스트 새로 작성해야 함
  @Test
  @Disabled("apple 로그인 기능 추가 후 테스트")
  void 애플_유저_정보를_조회해야_한다() throws Exception {
    //given
    UserLoginRequest request = new UserLoginRequest(AuthProvider.APPLE);

    //when
    UserInfoResponse response = sut.getUserInfoByAccessToken("access token", request);

    //then
    assertThat(response).isNull();
  }

  @Test
  void 소셜_정보로_유저를_조회해야_한다() throws Exception {
    //given
    User savedUser = userCommandRepository.save(UserFixture.createKakao());

    //when
    User user = sut.getUserBySocialInfoOrNull(savedUser.getAuth().getSocialId(), AuthProvider.KAKAO);

    //then
    assertThat(user).isEqualTo(savedUser);
  }

  @Test
  void 소셜_정보로_없는_유저를_조회하면_null을_반환해야_한다() throws Exception {
    //given

    //when
    User user = sut.getUserBySocialInfoOrNull("socialId", AuthProvider.KAKAO);

    //then
    assertThat(user).isNull();
  }
}
