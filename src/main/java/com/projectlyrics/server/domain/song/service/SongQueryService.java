package com.projectlyrics.server.domain.song.service;

import com.projectlyrics.server.domain.common.dto.util.CursorBasePaginatedResponse;
import com.projectlyrics.server.domain.common.dto.util.OffsetBasePaginatedResponse;
import com.projectlyrics.server.domain.search.domain.SongSearch;
import com.projectlyrics.server.domain.search.repository.SearchRepository;
import com.projectlyrics.server.domain.song.dto.response.SongGetResponse;
import com.projectlyrics.server.domain.song.dto.response.SongSearchResponse;
import com.projectlyrics.server.domain.song.entity.Song;
import com.projectlyrics.server.domain.song.exception.SongNotFoundException;
import com.projectlyrics.server.domain.song.repository.SongQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SongQueryService {

    private final SongQueryRepository songQueryRepository;
    private final SearchRepository searchRepository;

    public OffsetBasePaginatedResponse<SongSearchResponse> searchSongs(String query, int pageNumber, int pageSize) {
        List<Long> searchedIds = searchRepository.search(query, pageNumber, pageSize + 1).stream()
                .map(SongSearch::id)
                .toList();
        List<Song> songs = songQueryRepository.findAllByIds(searchedIds);
        List<SongSearchResponse> response = searchedIds.stream()
                .map(id -> songs.stream()
                        .filter(song -> song.getId().equals(id))
                        .findFirst()
                        .get())
                .map(SongSearchResponse::from)
                .toList();

        return OffsetBasePaginatedResponse.of(pageNumber, pageSize, response);
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
