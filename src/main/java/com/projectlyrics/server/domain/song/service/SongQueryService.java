package com.projectlyrics.server.domain.song.service;

import com.projectlyrics.server.domain.common.dto.util.CursorBasePaginatedResponse;
import com.projectlyrics.server.domain.common.dto.util.IdsWithHasNext;
import com.projectlyrics.server.domain.common.dto.util.OffsetBasePaginatedResponse;
import com.projectlyrics.server.domain.search.domain.SongSearch;
import com.projectlyrics.server.domain.song.dto.response.SongGetResponse;
import com.projectlyrics.server.domain.song.dto.response.SongSearchResponse;
import com.projectlyrics.server.domain.song.entity.Song;
import com.projectlyrics.server.domain.song.exception.SongNotFoundException;
import com.projectlyrics.server.domain.song.repository.SongMongoQueryRepository;
import com.projectlyrics.server.domain.song.repository.SongQueryRepository;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SongQueryService {

    private final SongQueryRepository songQueryRepository;
    private final SongMongoQueryRepository songMongoQueryRepository;

    public OffsetBasePaginatedResponse<SongSearchResponse> searchSongs(String query, int pageNumber, int pageSize) {
        if (Objects.isNull(query) || query.isEmpty()) {
            return getSongsByNoteCount(pageNumber, pageSize);
        }

        IdsWithHasNext idsWithHasNext = songMongoQueryRepository.searchSongsByName(
                query,
                pageNumber * pageSize,
                pageSize
        );

        List<Long> songsIds = idsWithHasNext.ids();

        if (songsIds.size() == 0 && pageNumber == 0) {
            return searchSongsWithLike(query, PageRequest.of(pageNumber, pageSize));
        }

        List<SongSearchResponse> songs = songQueryRepository.findAllByIdsInOrder(songsIds).stream()
                .map(SongSearchResponse::from)
                .toList();

        return OffsetBasePaginatedResponse.of(
                pageNumber,
                idsWithHasNext.hasNext(),
                songs
        );
    }

    public OffsetBasePaginatedResponse<SongSearchResponse> searchSongsWithLike(String query, Pageable pageable) {
        return OffsetBasePaginatedResponse.of(songQueryRepository.findAllByQuery(query, pageable)
                .map(SongSearchResponse::from)
        );
    }

    private OffsetBasePaginatedResponse<SongSearchResponse> getSongsByNoteCount(int pageNumber, int pageSize) {
        return OffsetBasePaginatedResponse.of(
                songQueryRepository.findAllOrderByNoteCountDesc(PageRequest.of(pageNumber, pageSize))
                        .map(SongSearchResponse::from)
        );
    }

    private List<SongSearchResponse> convertToResponse(List<SongSearch> searchResult) {
        List<Long> searchedIds = searchResult.stream()
                .map(SongSearch::id)
                .toList();

        Map<Long, Song> songs = songQueryRepository.findAllByIds(searchedIds)
                .stream()
                .collect(Collectors.toMap(Song::getId, song -> song));

        return searchedIds.stream()
                .map(songs::get)
                .map(SongSearchResponse::from)
                .toList();
    }

    public CursorBasePaginatedResponse<SongGetResponse> searchSongsByArtist(Long artistId, String query, Long cursor,
                                                                            int size) {
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
