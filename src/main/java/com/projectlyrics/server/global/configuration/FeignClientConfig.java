package com.projectlyrics.server.global.configuration;

import com.projectlyrics.server.domain.auth.service.kakao.KakaoSocialDataApiClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackageClasses = {
    KakaoSocialDataApiClient.class
})
public class FeignClientConfig {

}
