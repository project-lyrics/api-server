package com.projectlyrics.server.global.configuration;

import com.projectlyrics.server.domain.auth.service.social.apple.ApplePublicKeysApiClient;
import com.projectlyrics.server.domain.auth.service.social.kakao.KakaoSocialDataApiClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackageClasses = {
    KakaoSocialDataApiClient.class,
    ApplePublicKeysApiClient.class
})
public class FeignClientConfig {

}
