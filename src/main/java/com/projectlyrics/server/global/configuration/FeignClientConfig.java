package com.projectlyrics.server.global.configuration;

import com.projectlyrics.server.domain.auth.service.kakao.KakaoSocialDataApiClient;
import com.projectlyrics.server.domain.test.auth.kakao.KakaoAuthApiClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackageClasses = {
    KakaoAuthApiClient.class,
    KakaoSocialDataApiClient.class
})
public class FeignClientConfig {

}
