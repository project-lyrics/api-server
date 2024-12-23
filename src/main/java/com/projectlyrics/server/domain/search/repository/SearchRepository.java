package com.projectlyrics.server.domain.search.repository;

import com.projectlyrics.server.domain.search.domain.SongSearch;
import com.projectlyrics.server.domain.search.exception.FailedToIndexException;
import com.projectlyrics.server.domain.search.exception.FailedToSearchException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.Refresh;
import org.opensearch.client.opensearch.core.IndexRequest;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.search.Hit;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class SearchRepository {

    private final OpenSearchClient searchClient;

    @Async
    public void index(SongSearch songSearch) {
        try {
            searchClient.index(IndexRequest.of(builder ->
                    builder
                            .index(SongSearch.INDEX)
                            .id(songSearch.id().toString())
                            .document(songSearch)
                            .refresh(Refresh.True)
            ));
        } catch (IOException e) {
            log.error("error occurred indexing a document.");
            throw new FailedToIndexException();
        }
    }

    public List<SongSearch> search(String query) {
        try {
            SearchRequest request = new SearchRequest.Builder()
                    .index(SongSearch.INDEX)
                    .query(q -> q.queryString(qs -> qs.fields(SongSearch.FIELDS).query(query)))
                    .build();
            SearchResponse<SongSearch> response = searchClient.search(request, SongSearch.class);

            return response.hits().hits().stream()
                    .map(Hit::source)
                    .toList();
        } catch (IOException e) {
            log.error("error occurred searching a document.");
            throw new FailedToSearchException();
        }
    }
}
