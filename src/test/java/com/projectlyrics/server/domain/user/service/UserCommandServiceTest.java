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
  void 유효한_토큰에_대해_만료_날짜를_포함한_내용을_제공한다() {
    // given
    var claimsMock = Mockito.mock(Claims.class);
    claimsMock.setExpiration(new Date());

    given(jwtTokenProvider.validateToken("valid_token"))
        .willReturn(TokenValidationResult.of(VALID_JWT, claimsMock));

    // when
    var result = sut.validateToken("valid_token");

    // then
    assertThat(result.isValid()).isTrue();
    assertThat(result.expirationDate()).isEqualTo(claimsMock.getExpiration());
  }

  @Test
  void 유효하지_않은_토큰에_대해_isValid_값을_false로_제공한다() {
    // given
    given(jwtTokenProvider.validateToken("invalid_token"))
        .willReturn(TokenValidationResult.of(INVALID_JWT_TOKEN));

    // when
    var result = sut.validateToken("invalid_token");

    // then
    assertThat(result.isValid()).isFalse();
    assertThat(result.expirationDate()).isNull();
  }

  @Test
  void 리프레쉬_토큰으로부터_새로운_액세스_토큰을_발급한다() {
    // given
    var accessToken = "access_token";
    var refreshToken = "refresh_token";
    var userId = 1L;

    given(jwtTokenProvider.readUserIdFrom(refreshToken)).willReturn(userId);
    given(jwtTokenProvider.issueTokens(userId)).willReturn(new AuthToken(accessToken, null));

    // when
    UserTokenReissueResponse response = sut.reissueAccessToken(refreshToken);

    // then
    assertThat(response.accessToken()).isEqualTo(accessToken);
  }
}