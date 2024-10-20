package com.projectlyrics.server.global.configuration;

import com.projectlyrics.server.domain.auth.authentication.AuthArgumentResolver;
import com.projectlyrics.server.domain.auth.authentication.interceptor.AuthInterceptor;
import com.projectlyrics.server.domain.auth.authentication.interceptor.SlackInterceptor;
import com.projectlyrics.server.domain.auth.authentication.interceptor.AdminInterceptor;
import com.projectlyrics.server.global.converter.ProfileCharacterConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;
    private final AuthArgumentResolver authArgumentResolver;
    private final AdminInterceptor adminInterceptor;
    private final SlackInterceptor slackInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/actuator/health")
                .excludePathPatterns("/api/v1/auth/sign-in")
                .excludePathPatterns("/api/v1/auth/sign-up")
                .excludePathPatterns("/api/v1/auth/token")
                .excludePathPatterns("/api/v1/artists")
                .excludePathPatterns("/api/v1/artists/search")
                .excludePathPatterns("/api/v1/notes/artists")
                .excludePathPatterns("/api/v1/notes/songs")
                .excludePathPatterns("/api/v1/songs/search")
                .excludePathPatterns("/api/v1/slack/interactive");

        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/api/v1/artists/**")
                .addPathPatterns("/api/v1/notifications/public")
                .excludePathPatterns("/api/v1/artists/search");

        registry.addInterceptor(slackInterceptor)
                .addPathPatterns("/api/v1/slack/interactive");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authArgumentResolver);
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new ProfileCharacterConverter());
    }
}
