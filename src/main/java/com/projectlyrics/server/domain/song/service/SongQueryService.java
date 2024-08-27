package com.projectlyrics.server.domain.song.service;

import com.projectlyrics.server.domain.common.dto.util.CursorBasePaginatedResponse;
import com.projectlyrics.server.domain.common.dto.util.OffsetBasePaginatedResponse;
import com.projectlyrics.server.domain.song.dto.response.SongSearchResponse;
import com.projectlyrics.server.domain.song.repository.SongQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SongQueryService {

    private final SongQueryRepository songQueryRepository;

    public OffsetBasePaginatedResponse<SongSearchResponse> searchSongs(Long artistId, String query, int pageNumber, int pageSize) {
        return OffsetBasePaginatedResponse.of(
                songQueryRepository.findAllByQueryAndArtistId(artistId, query, PageRequest.of(pageNumber, pageSize))
        );
    }
}
