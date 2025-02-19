package com.projectlyrics.server.global.configuration;

import java.time.Clock;
import java.time.ZoneId;
import java.util.TimeZone;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Class for create Clock type spring bean. This bean is using for 'time machine test' and set timezone.
 */
@Configuration
public class ClockConfig {

    @Bean
    public Clock clock() {
        return Clock.system(ZoneId.of("Asia/Seoul"));
    }
}
