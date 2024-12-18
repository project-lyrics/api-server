package com.projectlyrics.server.domain.song.service;

import com.projectlyrics.server.domain.common.dto.util.CursorBasePaginatedResponse;
import com.projectlyrics.server.domain.common.dto.util.OffsetBasePaginatedResponse;
import com.projectlyrics.server.domain.song.dto.response.SongGetResponse;
import com.projectlyrics.server.domain.song.dto.response.SongSearchResponse;
import com.projectlyrics.server.domain.song.exception.SongNotFoundException;
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

    public OffsetBasePaginatedResponse<SongSearchResponse> searchSongs(String query, int pageNumber, int pageSize) {
        return OffsetBasePaginatedResponse.of(
                songQueryRepository.findAllByQueryOrderByNoteCountDesc(query, PageRequest.of(pageNumber, pageSize))
                        .map(SongSearchResponse::from)
        );
    }

    public CursorBasePaginatedResponse<SongGetResponse> searchSongsByArtist(Long artistId, String query, Long cursor, int size) {
        return CursorBasePaginatedResponse.of(
                songQueryRepository.findAllByQueryAndArtistId(artistId, query, cursor, PageRequest.of(0, size))
                        .map(SongGetResponse::from)
        );
    }

    public SongSearchResponse getById(Long id) {
        return songQueryRepository.findById(id)
                .map(SongSearchResponse::from)
                .orElseThrow(SongNotFoundException::new);
    }
}
