package com.projectlyrics.server.domain.user.usecase.command.impl;

import com.projectlyrics.server.domain.auth.dto.request.UserLoginRequest;
import com.projectlyrics.server.domain.auth.dto.response.UserInfoResponse;
import com.projectlyrics.server.domain.auth.jwt.JwtTokenProvider;
import com.projectlyrics.server.domain.auth.service.kakao.KakaoSocialService;
import com.projectlyrics.server.domain.user.dto.response.LoginResponse;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.repository.CommandUserRepository;
import com.projectlyrics.server.domain.user.repository.QueryUserRepository;
import com.projectlyrics.server.domain.user.usecase.command.UserAuthUseCase;
import com.projectlyrics.server.global.exception.BusinessException;
import com.projectlyrics.server.global.message.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class UserAuthService implements UserAuthUseCase {

  private final QueryUserRepository queryUserRepository;
  private final CommandUserRepository commandUserRepository;
  private final KakaoSocialService kakaoSocialService;
  private final JwtTokenProvider jwtTokenProvider;

  @Override
  public LoginResponse login(UserLoginRequest loginRequest) {
    UserInfoResponse userInfo = getUserInfo(loginRequest);

    return authenticateUser(userInfo);
  }

  private UserInfoResponse getUserInfo(UserLoginRequest loginRequest) {
    switch (loginRequest.authProvider()) {
      case KAKAO:
        return kakaoSocialService.login(loginRequest);

      default:
        throw new BusinessException(ErrorCode.UNSUPPORTED_AUTH_PROVIDER);
    }
  }

  private LoginResponse authenticateUser(UserInfoResponse userinfo) {
    long id = getUser(userinfo).getId();

    return LoginResponse.of(jwtTokenProvider.issueTokens(id));
  }

  private User getUser(UserInfoResponse userinfo) {
    return queryUserRepository.findBySocialIdAndAuthProviderAndNotDeleted(userinfo.socialId(),
            userinfo.authProvider())
        .orElseGet(() -> joinNewUser(userinfo));
  }

  private User joinNewUser(UserInfoResponse userinfo) {
    return commandUserRepository.save(userinfo.toEntity());
  }
}
