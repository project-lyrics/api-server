package com.projectlyrics.server.domain.user.service;

import com.projectlyrics.server.domain.auth.dto.request.UserLoginRequest;
import com.projectlyrics.server.domain.auth.dto.response.UserInfoResponse;
import com.projectlyrics.server.domain.auth.entity.enumerate.AuthProvider;
import com.projectlyrics.server.domain.auth.service.kakao.KakaoSocialService;
import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.repository.UserQueryRepository;
import com.projectlyrics.server.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserQueryService {

  private final UserQueryRepository userQueryRepository;
  private final KakaoSocialService kakaoSocialService;

  public UserInfoResponse getUserInfoByAccessToken(String socialAccessToken, UserLoginRequest loginRequest) {
    return switch (loginRequest.authProvider()) {
      case KAKAO -> kakaoSocialService.getSocialData(socialAccessToken);
      case APPLE -> null;
    };
  }

  public User getUserBySocialInfo(String socialId, AuthProvider authProvider) {
    return userQueryRepository.findBySocialIdAndAuthProviderAndNotDeleted(socialId, authProvider)
        .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
  }

  public User getUserById(long userId) {
    return userQueryRepository.findById(userId)
        .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
  }
}
