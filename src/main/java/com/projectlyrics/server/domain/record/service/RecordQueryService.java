package com.projectlyrics.server.domain.record.service;

import com.projectlyrics.server.domain.common.dto.util.CursorBasePaginatedResponse;
import com.projectlyrics.server.domain.record.domain.Record;
import com.projectlyrics.server.domain.record.dto.request.RecordGetResponse;
import com.projectlyrics.server.domain.record.exception.RecordNotFoundException;
import com.projectlyrics.server.domain.record.repository.RecordQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class RecordQueryService {

    private final RecordQueryRepository recordQueryRepository;

    public Record getRecordByUserIdAndArtistId(long userId, long artistId) {
        return recordQueryRepository.findByUserIdAndArtistIdAndNotDeleted(userId, artistId)
                .orElseThrow(RecordNotFoundException::new);
    }

    public CursorBasePaginatedResponse<RecordGetResponse> getRecordsByUserId(long userId, long cursor, Pageable pageable) {
        Slice<RecordGetResponse> records = recordQueryRepository.findAllByUserIdAndNotDeleted(userId, cursor, pageable)
                .map(RecordGetResponse::of);

        return CursorBasePaginatedResponse.of(records);
    }
}
