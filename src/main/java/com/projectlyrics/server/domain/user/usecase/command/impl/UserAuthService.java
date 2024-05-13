package com.projectlyrics.server.domain.user.usecase.command.impl;

import com.projectlyrics.server.domain.auth.dto.request.UserLoginRequest;
import com.projectlyrics.server.domain.auth.dto.response.UserInfoResponse;
import com.projectlyrics.server.domain.auth.jwt.JwtTokenProvider;
import com.projectlyrics.server.domain.auth.jwt.dto.AuthToken;
import com.projectlyrics.server.domain.auth.service.kakao.KakaoSocialService;
import com.projectlyrics.server.domain.user.dto.response.LoginResponse;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.repository.CommandUserRepository;
import com.projectlyrics.server.domain.user.repository.QueryUserRepository;
import com.projectlyrics.server.domain.user.usecase.command.UserAuthUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserAuthService implements UserAuthUseCase {

  private final QueryUserRepository queryUserRepository;
  private final CommandUserRepository commandUserRepository;
  private final KakaoSocialService kakaoSocialService;
  private final JwtTokenProvider jwtTokenProvider;

  @Override
  public LoginResponse signIn(String socialAccessToken, UserLoginRequest request) {
    UserInfoResponse userInfo = getUserInfo(socialAccessToken, request);

    return LoginResponse.of(getToken(userInfo));
  }

  private UserInfoResponse getUserInfo(String socialAccessToken, UserLoginRequest loginRequest) {
    return switch (loginRequest.authProvider()) {
      case KAKAO -> kakaoSocialService.getSocialData(socialAccessToken);
      case APPLE -> null;
    };
  }

  private AuthToken getToken(UserInfoResponse userinfo) {
    long id = getUser(userinfo).getId();

    return jwtTokenProvider.issueTokens(id);
  }

  private User getUser(UserInfoResponse userinfo) {
    return queryUserRepository.findBySocialIdAndAuthProviderAndNotDeleted(userinfo.socialId(), userinfo.authProvider())
        .orElseGet(() -> joinNewUser(userinfo));
  }

  private User joinNewUser(UserInfoResponse userinfo) {
    return commandUserRepository.save(userinfo.toEntity());
  }
}
