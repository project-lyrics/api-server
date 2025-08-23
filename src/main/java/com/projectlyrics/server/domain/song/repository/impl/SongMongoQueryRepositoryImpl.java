package com.projectlyrics.server.domain.song.repository.impl;

import com.projectlyrics.server.domain.common.dto.util.IdsWithHasNext;
import com.projectlyrics.server.domain.song.entity.SongMongo;
import com.projectlyrics.server.domain.song.repository.SongMongoQueryRepository;
import java.util.Arrays;
import java.util.List;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class SongMongoQueryRepositoryImpl implements SongMongoQueryRepository {

    private final MongoTemplate mongoTemplate;

    public SongMongoQueryRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public IdsWithHasNext searchSongsByName(String query, int offset, int limit) {
        // nori + english + edgeGram autocomplete
        int queryLength = query.length();

        // 길이에 따라 가중치 동적 조정
        int autocompleteBoost = queryLength <= 3 ? 2 : 1;
        int textBoost = queryLength <= 3 ? 1 : 2;

        AggregationOperation searchStage = context -> new Document("$search",
                new Document("index", "songs_search_index")
                        .append("compound", new Document("should", Arrays.asList(
                                new Document("text", new Document()
                                        .append("query", query)
                                        .append("path", Arrays.asList(
                                                "name",
                                                new Document("value", "name").append("multi", "englishAnalyzer")
                                        ))
                                        .append("score", new Document("boost", new Document("value", textBoost)))
                                ),
                                new Document("autocomplete", new Document()
                                        .append("query", query)
                                        .append("path", "name")
                                        .append("tokenOrder", "any")
                                        .append("score",
                                                new Document("boost", new Document("value", autocompleteBoost)))
                                )
                        )))
        );

        AggregationOperation projectStage = Aggregation.project("_id");
        AggregationOperation skipStage = Aggregation.skip(offset);
        AggregationOperation limitStage = Aggregation.limit(limit + 1);

        Aggregation aggregation = Aggregation.newAggregation(
                searchStage,
                projectStage,
                skipStage,
                limitStage
        );

        AggregationResults<Document> results =
                mongoTemplate.aggregate(aggregation, "songs", Document.class);

        List<Long> ids = results.getMappedResults().stream()
                .map(doc -> doc.get("_id", Long.class))
                .toList();

        boolean hasNext = ids.size() > limit;

        if (hasNext) {
            ids = ids.subList(0, limit);
        }

        return new IdsWithHasNext(ids, hasNext);
    }

    public List<Long> findAllIdByIdIn(List<Long> ids) {
        Query query = new Query(Criteria.where("_id").in(ids));
        query.fields().include("_id");
        return mongoTemplate.find(query, SongMongo.class)
                .stream()
                .map(SongMongo::getId)
                .toList();
    }
}
