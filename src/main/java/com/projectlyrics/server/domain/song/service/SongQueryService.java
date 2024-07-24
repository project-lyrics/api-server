package com.projectlyrics.server.domain.song.service;

import com.projectlyrics.server.domain.common.dto.util.CursorBasePaginatedResponse;
import com.projectlyrics.server.domain.song.dto.response.SongGetResponse;
import com.projectlyrics.server.domain.song.repository.SongQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SongQueryService {

    private final SongQueryRepository songQueryRepository;

    public CursorBasePaginatedResponse<SongGetResponse> searchSongs(String query, Long cursorId, int size) {
        Slice<SongGetResponse> searchedSongs = songQueryRepository.findAllByQuery(query, cursorId, PageRequest.ofSize(size))
                .map(SongGetResponse::from);

        return CursorBasePaginatedResponse.of(searchedSongs);
    }
}
