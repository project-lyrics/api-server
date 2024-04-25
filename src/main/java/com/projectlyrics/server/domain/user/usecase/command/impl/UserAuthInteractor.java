package com.projectlyrics.server.domain.user.usecase.command.impl;

import com.projectlyrics.server.domain.user.dto.response.LoginResponse;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.auth.external.dto.request.UserLoginRequest;
import com.projectlyrics.server.domain.auth.external.dto.response.UserInfoResponse;
import com.projectlyrics.server.domain.auth.external.kakao.KakaoSocialService;
import com.projectlyrics.server.domain.auth.jwt.JwtTokenProvider;
import com.projectlyrics.server.domain.user.repository.UserCommandQueryRepository;
import com.projectlyrics.server.domain.user.usecase.command.UserAuthUseCase;
import com.projectlyrics.server.global.error_code.ErrorCode;
import com.projectlyrics.server.global.exception.BusinessException;
import com.projectlyrics.server.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class UserAuthInteractor implements UserAuthUseCase {

  private final UserCommandQueryRepository userRepository;
  private final KakaoSocialService kakaoSocialService;
  private final JwtTokenProvider jwtTokenProvider;

  @Override
  public LoginResponse login(UserLoginRequest loginRequest) {
    UserInfoResponse userInfo = getUserInfo(loginRequest);

    return authenticateUser(userInfo);
  }

  private UserInfoResponse getUserInfo(UserLoginRequest loginRequest) {
    switch (loginRequest.authProvider()) {
      case KAKAO :
        return kakaoSocialService.login(loginRequest);

      default :
        throw new BusinessException(ErrorCode.UNSUPPORTED_AUTH_PROVIDER);
    }
  }

  private LoginResponse authenticateUser(UserInfoResponse userinfo) {
    long id;

    try {
      id = getUserBy(userinfo).getId();
    } catch (NotFoundException e) {
      id = joinNewUser(userinfo);
    }

    return LoginResponse.of(jwtTokenProvider.issueTokens(id));
  }

  private Long joinNewUser(UserInfoResponse userinfo) {
    return userRepository.save(userinfo.toEntity()).getId();
  }

  private User getUserBy(UserInfoResponse userinfo) {
    return userRepository.findBySocialIdAndAuthProviderAndNotDeleted(userinfo.socialId(), userinfo.authProvider())
        .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
  }
}
