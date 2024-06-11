package com.projectlyrics.server.domain.user.service;

import static com.projectlyrics.server.domain.auth.jwt.JwtValidationType.INVALID_JWT_TOKEN;
import static com.projectlyrics.server.domain.auth.jwt.JwtValidationType.VALID_JWT;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;

import com.projectlyrics.server.domain.auth.dto.response.UserTokenReissueResponse;
import com.projectlyrics.server.domain.auth.jwt.JwtTokenProvider;
import com.projectlyrics.server.domain.auth.jwt.dto.AuthToken;
import com.projectlyrics.server.domain.auth.jwt.dto.TokenValidationResult;
import com.projectlyrics.server.domain.user.repository.UserCommandRepository;
import io.jsonwebtoken.Claims;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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

    given(jwtTokenProvider.readUserIdFrom(refreshToken)).willReturn(userId);
    given(jwtTokenProvider.issueTokens(userId)).willReturn(new AuthToken(accessToken, null));

    // when
    UserTokenReissueResponse response = sut.reissueAccessToken(refreshToken);

    // then
    assertThat(response.accessToken()).isEqualTo(accessToken);
  }
}