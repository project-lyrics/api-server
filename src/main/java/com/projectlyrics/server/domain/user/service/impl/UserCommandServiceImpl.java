package com.projectlyrics.server.domain.user.service.impl;

import com.projectlyrics.server.domain.user.dto.response.LoginResponse;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.repository.CommandQueryUserRepository;
import com.projectlyrics.server.domain.user.service.UserCommandService;
import com.projectlyrics.server.domain.auth.authentication.UserAuthentication;
import com.projectlyrics.server.domain.auth.external.dto.request.UserLoginRequest;
import com.projectlyrics.server.domain.auth.external.dto.response.UserInfoResponse;
import com.projectlyrics.server.domain.auth.external.kakao.KakaoSocialService;
import com.projectlyrics.server.domain.auth.jwt.JwtTokenProvider;
import com.projectlyrics.server.domain.auth.jwt.redis.service.TokenService;
import com.projectlyrics.server.global.error_code.ErrorCode;
import com.projectlyrics.server.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class UserCommandServiceImpl implements UserCommandService {

  private final CommandQueryUserRepository userRepository;
  private final KakaoSocialService kakaoSocialService;
  private final TokenService tokenService;
  private final JwtTokenProvider jwtTokenProvider;

  @Override
  public LoginResponse login(UserLoginRequest loginRequest) {
    UserInfoResponse userInfo = getUserInfo(loginRequest);

    return getAccessAndRefreshTokens(userInfo);
  }

  private UserInfoResponse getUserInfo(UserLoginRequest loginRequest) {
    switch (loginRequest.authProvider()) {
      case KAKAO :
        return kakaoSocialService.login(loginRequest);

      default :
        throw new BusinessException(ErrorCode.UNSUPPORTED_AUTH_PROVIDER);
    }
  }

  private LoginResponse getAccessAndRefreshTokens(UserInfoResponse userinfo) {
    if (isExistingUserBy(userinfo)) {
      User user = getUserBy(userinfo);
      return getAccessAndRefreshTokensByUserId(user.getId());
    }

    Long id = join(userinfo);
    return getAccessAndRefreshTokensByUserId(id);
  }

  private Long join(UserInfoResponse userinfo) {
    return userRepository.save(userinfo.toEntity()).getId();
  }

  private LoginResponse getAccessAndRefreshTokensByUserId(Long id) {
    UserAuthentication authentication = UserAuthentication.of(id);

    return new LoginResponse(
        jwtTokenProvider.issueAccessToken(authentication),
        issueRefreshToken(authentication)
    );
  }

  private String issueRefreshToken(UserAuthentication authentication) {
    String refreshToken = jwtTokenProvider.issueRefreshToken(authentication);
    tokenService.saveRefreshToken((Long) authentication.getPrincipal(), refreshToken);

    return refreshToken;
  }

  private boolean isExistingUserBy(UserInfoResponse userInfo) {
    return userRepository.findBySocialIdAndAuthProviderAndNotDeleted(userInfo.socialId(), userInfo.authProvider())
        .isPresent();
  }

  private User getUserBy(UserInfoResponse userinfo) {
    return userRepository.findBySocialIdAndAuthProviderAndNotDeleted(userinfo.socialId(), userinfo.authProvider())
        .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
  }
}
