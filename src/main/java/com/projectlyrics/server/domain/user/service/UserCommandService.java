package com.projectlyrics.server.domain.user.service;

import com.projectlyrics.server.domain.auth.dto.request.UserLoginRequest;
import com.projectlyrics.server.domain.auth.dto.response.UserInfoResponse;
import com.projectlyrics.server.domain.auth.jwt.JwtTokenProvider;
import com.projectlyrics.server.domain.auth.jwt.dto.AuthToken;
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
  public AuthToken getToken(UserInfoResponse userinfo) {
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
}
