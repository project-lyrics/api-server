package com.projectlyrics.server.global.configuration;

import com.projectlyrics.server.domain.auth.authentication.AuthArgumentResolver;
import com.projectlyrics.server.domain.auth.authentication.AuthInterceptor;
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

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/actuator/health")
                .excludePathPatterns("/api/v1/auth/sign-in")
                .excludePathPatterns("/api/v1/auth/sign-up")
                .excludePathPatterns("/api/v1/auth/token")
                .excludePathPatterns("/api/v1/artists/search")
                .excludePathPatterns("/api/v1/notes/artists")
                .excludePathPatterns("/api/v1/notes/songs")
                .excludePathPatterns("/api/v1/songs/search");
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
