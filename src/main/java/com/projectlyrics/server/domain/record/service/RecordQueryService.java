package com.projectlyrics.server.domain.record.service;

import com.projectlyrics.server.domain.common.dto.util.CursorBasePaginatedResponse;
import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.domain.common.util.PageUtils;
import com.projectlyrics.server.domain.record.domain.Record;
import com.projectlyrics.server.domain.record.dto.request.RecordGetResponse;
import com.projectlyrics.server.domain.record.repository.RecordQueryRepository;
import com.projectlyrics.server.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class RecordQueryService {

  private final RecordQueryRepository recordQueryRepository;

  public Record getRecordByUserIdAndArtistId(long userId, long artistId) {
    return recordQueryRepository.findByUserIdAndArtistIdAndNotDeleted(userId, artistId)
        .orElseThrow(() -> new NotFoundException(ErrorCode.RECORD_NOT_FOUND));
  }

  public CursorBasePaginatedResponse<RecordGetResponse> getRecordsByUserId(long userId, long cursor, Pageable pageable) {
    var records = recordQueryRepository.findAllByUserIdAndNotDeleted(userId, cursor, pageable)
        .map(RecordGetResponse::of);
    var nextCursor = PageUtils.getNextCursorOf(records);

    return CursorBasePaginatedResponse.of(records, nextCursor, cursor);
  }
}
