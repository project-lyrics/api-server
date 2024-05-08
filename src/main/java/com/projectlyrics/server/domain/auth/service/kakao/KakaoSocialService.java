package com.projectlyrics.server.domain.auth.service.kakao;

import com.projectlyrics.server.domain.auth.dto.request.UserLoginRequest;
import com.projectlyrics.server.domain.auth.dto.response.UserInfoResponse;
import com.projectlyrics.server.domain.auth.entity.enumerate.AuthProvider;
import com.projectlyrics.server.domain.auth.service.SocialService;
import com.projectlyrics.server.domain.auth.service.kakao.dto.KakaoAccessTokenResponse;
import com.projectlyrics.server.domain.auth.service.kakao.dto.KakaoUserInfoResponse;
import com.projectlyrics.server.global.exception.BusinessException;
import com.projectlyrics.server.global.message.ErrorCode;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class KakaoSocialService extends SocialService {

  private static final String GRANT_TYPE = "authorization_code";
  private static final String TOKEN_TYPE = "Bearer ";

  @Value("${kakao.client_id}")
  private String clientId;
  private final KakaoAuthApiClient kakaoAuthApiClient;
  private final KakaoApiClient kakaoApiClient;
  private String accessToken;

  @Override
  public UserInfoResponse login(UserLoginRequest loginRequest) {
    try {
      accessToken = getAccessTokenWith(loginRequest);
    } catch (FeignException e) {
      throw new BusinessException(ErrorCode.AUTHENTICATION_CODE_EXPIRED);
    }

    KakaoUserInfoResponse userInfo = getUserInfoFrom(accessToken);
    return getLoginResult(loginRequest.authProvider(), userInfo);
  }

  private String getAccessTokenWith(UserLoginRequest loginRequest) {
    KakaoAccessTokenResponse response = kakaoAuthApiClient.getOauth2AccessToken(
        GRANT_TYPE,
        clientId,
        loginRequest.redirectUri(),
        loginRequest.authorizationCode()
    );

    return response.accessToken();
  }

  private KakaoUserInfoResponse getUserInfoFrom(String accessToken) {
    return kakaoApiClient.getUserInfo(TOKEN_TYPE + accessToken);
  }

  private UserInfoResponse getLoginResult(AuthProvider authProvider, KakaoUserInfoResponse userInfoResponse) {
    return new UserInfoResponse(
        userInfoResponse.id(),
        authProvider,
        userInfoResponse.kakaoAccount().email()
    );
  }
}
