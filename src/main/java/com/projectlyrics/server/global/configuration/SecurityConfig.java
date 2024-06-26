package com.projectlyrics.server.global.configuration;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import com.projectlyrics.server.domain.auth.authentication.JwtAuthenticationEntryPoint;
import com.projectlyrics.server.domain.auth.authentication.JwtAuthenticationFilter;
import com.projectlyrics.server.global.handler.FilterExceptionHandler;
import com.projectlyrics.server.domain.auth.authentication.UndefinedAccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Value("#{'${auth.free-apis}'.split(',')}")
    private String[] authFreeApis;

    @Value("#{'${auth.admin-apis}'.split(',')}")
    private String[] authAdminApis;

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final FilterExceptionHandler filterExceptionHandler;
    private final UndefinedAccessHandler undefinedAccessHandler;

    @Bean
    @Profile({"local", "test"})
    public SecurityFilterChain localHttpSecurity(HttpSecurity http) throws Exception {
        permitDevelopApis(http);
        setHttp(http);

        return http.build();
    }

    @Bean
    @Profile("stage")
    public SecurityFilterChain stageHttpSecurity(HttpSecurity http) throws Exception {
        permitDevelopApis(http);
        setHttp(http);

        return http.build();
    }

    @Bean
    @Profile("prod")
    public SecurityFilterChain prodHttpSecurity(HttpSecurity http) throws Exception {
        setHttp(http);

        return http.build();
    }

    private void permitDevelopApis(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                .requestMatchers(new AntPathRequestMatcher("/v3/api-docs/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/swagger-ui/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/docs/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/actuator/health")).permitAll());
    }

    // TODO : 비회원 허용 API를 추가적으로 등록해야 함
    private void setHttp(HttpSecurity http) throws Exception {
        http
                .csrf(CsrfConfigurer::disable)
                .formLogin(FormLoginConfigurer::disable)
                .httpBasic(HttpBasicConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .exceptionHandling(exception -> {
                    exception.authenticationEntryPoint(jwtAuthenticationEntryPoint);
                    exception.accessDeniedHandler(undefinedAccessHandler);
                })
                .authorizeHttpRequests(requests ->
                        requests
                                .requestMatchers(authFreeApis).permitAll()
                                .requestMatchers(authAdminApis).hasRole("ADMIN")
                                .requestMatchers("/api/**").authenticated()
                                .anyRequest().denyAll()
                )
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(filterExceptionHandler, JwtAuthenticationFilter.class);
    }
}
