package com.projectlyrics.server.global.dev;

import com.projectlyrics.server.ServerApplication;
import com.projectlyrics.server.domain.search.domain.SongSearch;
import com.projectlyrics.server.domain.search.repository.SearchRepository;
import com.projectlyrics.server.domain.song.repository.SongQueryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;

import static org.springframework.boot.WebApplicationType.NONE;

@Slf4j
public class SongIndexer {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = new SpringApplicationBuilder()
                .sources(ServerApplication.class)
                .web(NONE)
                .run(args);

        index(context);

        context.close();
    }

    private static void index(ConfigurableApplicationContext context) {
        SearchRepository searchRepository = context.getBean(SearchRepository.class);
        SongQueryRepository songQueryRepository = context.getBean(SongQueryRepository.class);

        List<SongSearch> searches = songQueryRepository.findAll().stream()
                .map(SongSearch::from)
                .toList();

        int limit = 100;
        for (int offset = 31600; offset < searches.size(); offset += limit) {
            searchRepository.bulkIndex(searches.subList(offset, offset + limit));
        }
    }
}
