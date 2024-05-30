package com.projectlyrics.server.domain.user.service;

import static com.projectlyrics.server.domain.auth.jwt.JwtValidationType.VALID_JWT;

import com.projectlyrics.server.domain.auth.dto.request.UserLoginRequest;
import com.projectlyrics.server.domain.auth.dto.response.UserInfoResponse;
import com.projectlyrics.server.domain.auth.dto.response.UserTokenReissueResponse;
import com.projectlyrics.server.domain.auth.dto.response.UserTokenValidationResponse;
import com.projectlyrics.server.domain.auth.jwt.JwtTokenProvider;
import com.projectlyrics.server.domain.auth.jwt.dto.AuthToken;
import com.projectlyrics.server.domain.auth.jwt.dto.TokenValidationResult;
import com.projectlyrics.server.domain.common.util.TokenUtils;
import com.projectlyrics.server.domain.user.dto.response.UserLoginResponse;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.repository.UserCommandRepository;
import com.projectlyrics.server.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class UserCommandService {

  private final UserCommandRepository userCommandRepository;
  private final JwtTokenProvider jwtTokenProvider;
  private final UserQueryService userQueryService;

  public UserLoginResponse signIn(String socialAccessToken, UserLoginRequest request) {
    UserInfoResponse userInfo = userQueryService.getUserInfoByAccessToken(socialAccessToken, request);

    return UserLoginResponse.of(getToken(userInfo));
  }

  @Transactional(noRollbackFor = NotFoundException.class)
  protected AuthToken getToken(UserInfoResponse userinfo) {
    long id;

    try {
      id = userQueryService.getUserBySocialInfo(userinfo.socialId(), userinfo.authProvider()).getId();
    } catch (NotFoundException e) {
      id = createUser(userinfo.toEntity()).getId();
    }

    return jwtTokenProvider.issueTokens(id);
  }

  private User createUser(User user) {
    return userCommandRepository.save(user);
  }

  public UserTokenValidationResponse validateToken(String token) {
    String extractedToken = TokenUtils.extractToken(token);

    TokenValidationResult validationResult = jwtTokenProvider.validateToken(extractedToken);

    if (validationResult.jwtValidationType() == VALID_JWT) {
      return UserTokenValidationResponse.of(true, validationResult.claims().getExpiration());
    }

    return UserTokenValidationResponse.of(false, null);
  }

  public UserTokenReissueResponse reissueAccessToken(String refreshToken) {
    String extractedToken = TokenUtils.extractToken(refreshToken);

    Long userId = jwtTokenProvider.readUserIdFrom(extractedToken);
    String accessToken = jwtTokenProvider.issueTokens(userId).accessToken();

    return new UserTokenReissueResponse(accessToken);
  }
}
