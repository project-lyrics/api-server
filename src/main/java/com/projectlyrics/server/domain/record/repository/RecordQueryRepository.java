package com.projectlyrics.server.domain.record.repository;

import com.projectlyrics.server.domain.record.domain.Record;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

@Repository
public interface RecordQueryRepository {

    Optional<Record> findByUserIdAndArtistIdAndNotDeleted(long userId, long artistId);

    Slice<Record> findAllByUserIdAndNotDeleted(long userId, Long cursor, Pageable pageable);
}
