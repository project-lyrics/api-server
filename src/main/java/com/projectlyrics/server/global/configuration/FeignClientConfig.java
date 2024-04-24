package com.projectlyrics.server.global.configuration;

import com.projectlyrics.server.domain.auth.external.kakao.KakaoApiClient;
import com.projectlyrics.server.domain.auth.external.kakao.KakaoAuthApiClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackageClasses = {
    KakaoAuthApiClient.class,
    KakaoApiClient.class
})
public class FeignClientConfig {

}
