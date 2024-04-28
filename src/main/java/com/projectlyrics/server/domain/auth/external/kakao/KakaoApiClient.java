package com.projectlyrics.server.domain.auth.external.kakao;

import com.projectlyrics.server.domain.auth.external.kakao.dto.KakaoUserInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "kakaoApiClient", url = "https://kapi.kakao.com")
public interface KakaoApiClient {

  @GetMapping(value = "/v2/user/me")
  KakaoUserInfoResponse getUserInfo(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken);
}
