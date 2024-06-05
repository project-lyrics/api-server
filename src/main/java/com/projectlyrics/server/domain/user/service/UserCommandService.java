package com.projectlyrics.server.domain.user.service;

import com.projectlyrics.server.domain.auth.dto.request.UserLoginRequest;
import com.projectlyrics.server.domain.auth.dto.response.UserInfoResponse;
import com.projectlyrics.server.domain.auth.dto.response.UserTokenReissueResponse;
import com.projectlyrics.server.domain.auth.jwt.JwtTokenProvider;
import com.projectlyrics.server.domain.auth.jwt.dto.AuthToken;
import com.projectlyrics.server.domain.common.util.TokenUtils;
import com.projectlyrics.server.domain.user.dto.response.UserLoginResponse;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.repository.UserCommandRepository;
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

  protected AuthToken getToken(UserInfoResponse userinfo) {
    User user = userQueryService.getUserBySocialInfoOrNull(userinfo.socialId(), userinfo.authProvider());

    if (user == null)
      user = createUser(userinfo.toEntity());

    return jwtTokenProvider.issueTokens(user.getId());
  }

  private User createUser(User user) {
    return userCommandRepository.save(user);
  }

  public UserTokenReissueResponse reissueAccessToken(String refreshToken) {
    String extractedToken = TokenUtils.extractToken(refreshToken);

    Long userId = jwtTokenProvider.readUserIdFrom(extractedToken);
    String accessToken = jwtTokenProvider.issueTokens(userId).accessToken();

    return new UserTokenReissueResponse(accessToken);
  }
}
