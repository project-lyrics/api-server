package com.projectlyrics.server.domain.record.repository.impl;

import com.projectlyrics.server.domain.common.util.QueryDslUtils;
import com.projectlyrics.server.domain.record.domain.QRecord;
import com.projectlyrics.server.domain.record.domain.Record;
import com.projectlyrics.server.domain.record.repository.RecordQueryRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QueryDslRecordQueryRepository implements RecordQueryRepository {

  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public Optional<Record> findByUserIdAndArtistIdAndNotDeleted(long userId, long artistId) {
    return Optional.ofNullable(
        jpaQueryFactory
            .selectFrom(QRecord.record)
            .where(
                QRecord.record.user.id.eq(userId),
                QRecord.record.artist.id.eq(artistId),
                QRecord.record.commonField.deletedAt.isNull()
            )
            .fetchOne()
    );
  }

  @Override
  public Slice<Record> findAllByUserIdAndNotDeleted(long userId, Long cursor, Pageable pageable) {
    var content = jpaQueryFactory
        .selectFrom(QRecord.record)
        .where(
            cursor == null ? null : QRecord.record.id.goe(cursor),
            QRecord.record.user.id.eq(userId),
            QRecord.record.commonField.deletedAt.isNull()
        )
        .limit(pageable.getPageSize() + 1)
        .fetch();

    return new SliceImpl<>(content, pageable, QueryDslUtils.checkIfHasNext(pageable, content));
  }
}
