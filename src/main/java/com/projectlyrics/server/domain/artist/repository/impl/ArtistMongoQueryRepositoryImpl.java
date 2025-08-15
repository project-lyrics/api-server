package com.projectlyrics.server.domain.artist.repository.impl;

import com.projectlyrics.server.domain.artist.repository.ArtistMongoQueryRepository;
import com.projectlyrics.server.domain.common.dto.util.IdsWithHasNext;
import java.util.List;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Repository;

@Repository
public class ArtistMongoQueryRepositoryImpl implements ArtistMongoQueryRepository {

    private final MongoTemplate mongoTemplate;

    public ArtistMongoQueryRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public IdsWithHasNext searchArtistIdsByName(String query, int offset, int limit) {
        // edgeGram autocomplete
        AggregationOperation searchStage = context -> new Document("$search",
                new Document("index", "artists_search_index")
                        .append("autocomplete", new Document()
                                .append("query", query)
                                .append("path", "search_names")
                                .append("tokenOrder", "any")
                        )
        );

        AggregationOperation projectStage = Aggregation.project("_id");
        AggregationOperation skipStage = Aggregation.skip(offset);
        AggregationOperation limitStage = Aggregation.limit(limit + 1);

        Aggregation aggregation = Aggregation.newAggregation(searchStage, projectStage, skipStage, limitStage);

        AggregationResults<Document> results =
                mongoTemplate.aggregate(aggregation, "artists", Document.class);

        List<Long> ids = results.getMappedResults().stream()
                .map(doc -> doc.get("_id", Long.class))
                .toList();

        boolean hasNext = ids.size() > limit;

        if (hasNext) {
            ids = ids.subList(0, limit);
        }

        return new IdsWithHasNext(ids, hasNext);
    }
}

