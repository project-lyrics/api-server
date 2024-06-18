package com.projectlyrics.server.domain.record.repository.impl;

import com.projectlyrics.server.domain.record.domain.Record;
import org.springframework.data.repository.Repository;

public interface JpaRecordCommandRepository extends Repository<Record, Long> {

    Record save(Record entity);
}
