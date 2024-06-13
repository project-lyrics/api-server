package com.projectlyrics.server.domain.user.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;

import com.projectlyrics.server.domain.auth.dto.response.UserTokenReissueResponse;
import com.projectlyrics.server.domain.auth.jwt.JwtTokenProvider;
import com.projectlyrics.server.domain.auth.jwt.dto.AuthToken;
import com.projectlyrics.server.domain.user.repository.UserCommandRepository;
import com.projectlyrics.server.domain.user.service.UserCommandService;
import com.projectlyrics.server.domain.user.service.UserQueryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserCommandServiceTest {

  @InjectMocks
  private UserCommandService sut;

  @Mock
  private UserCommandRepository userCommandRepository;

  @Mock
  private JwtTokenProvider jwtTokenProvider;

  @Mock
  private UserQueryService userQueryService;

  @Test
  void 리프레쉬_토큰으로부터_새로운_액세스_토큰을_발급한다() {
    // given
    String accessToken = "access_token";
    String refreshToken = "refresh_token";
    long userId = 1L;

    given(jwtTokenProvider.getUserIdFromJwt(refreshToken)).willReturn(userId);
    given(jwtTokenProvider.issueTokens(userId)).willReturn(new AuthToken(accessToken, null));

    // when
    UserTokenReissueResponse response = sut.reissueAccessToken(refreshToken);

    // then
    assertThat(response.accessToken()).isEqualTo(accessToken);
  }
}