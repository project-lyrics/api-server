package com.projectlyrics.server.domain.auth.service.social.kakao;

import com.projectlyrics.server.domain.auth.service.social.kakao.dto.KakaoUserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "kakaoSocialDataApiClient", url = "https://kapi.kakao.com")
public interface KakaoSocialDataApiClient {

    @GetMapping(value = "/v2/user/me")
    KakaoUserInfo getUserInfo(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken);
}
