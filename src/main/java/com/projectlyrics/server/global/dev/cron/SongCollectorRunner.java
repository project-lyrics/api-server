package com.projectlyrics.server.global.dev.cron;

import static org.springframework.boot.WebApplicationType.NONE;

import com.projectlyrics.server.ServerApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@Slf4j
public class SongCollectorRunner {

    private static ConfigurableApplicationContext context;

    public static void main(String[] args) {
        log.info("song collector starts collecting!");

        context = new SpringApplicationBuilder()
                .sources(ServerApplication.class)
                .web(NONE)
                .run(args);

        SongCollector songCollector = context.getBean(SongCollector.class);
        songCollector.collect();

        //context.close();
    }
}
