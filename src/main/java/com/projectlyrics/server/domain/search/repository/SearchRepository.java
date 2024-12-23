package com.projectlyrics.server.domain.search.repository;

import com.projectlyrics.server.domain.search.domain.SongSearch;
import com.projectlyrics.server.domain.search.exception.FailedToIndexException;
import com.projectlyrics.server.domain.search.exception.FailedToSearchException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.Refresh;
import org.opensearch.client.opensearch.core.*;
import org.opensearch.client.opensearch.core.bulk.BulkOperation;
import org.opensearch.client.opensearch.core.bulk.CreateOperation;
import org.opensearch.client.opensearch.core.search.Hit;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Component
public class SearchRepository {

    private final OpenSearchClient searchClient;

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

    public void bulkIndex(List<SongSearch> songSearchList) {
        try {
            BulkRequest.Builder bulkRequestBuilder = new BulkRequest.Builder();

            songSearchList.forEach(search -> {
                BulkOperation operation = new BulkOperation.Builder()
                        .create(new CreateOperation.Builder<>()
                                .index(SongSearch.INDEX)
                                .id(search.id().toString())
                                .document(search)
                                .build())
                        .build();

                bulkRequestBuilder.operations(operation);
            });

            BulkResponse response = searchClient.bulk(bulkRequestBuilder.build());

            if (response.errors()) {
                response.items().forEach(item -> {
                    if (Objects.nonNull(item.error())) {
                        log.error(item.error().reason());
                    }
                });
            }
        } catch (IOException e) {
            log.error("error occurred bulk-indexing a document.");
            throw new FailedToIndexException();
        }
    }

    public List<SongSearch> search(String query, int pageNumber, int pageSize) {
        try {
            SearchRequest request = new SearchRequest.Builder()
                    .index(SongSearch.INDEX)
                    .query(q -> q.queryString(qs -> qs.fields(SongSearch.FIELDS).query(query)))
                    .from(pageNumber * pageSize)
                    .size(pageSize)
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
