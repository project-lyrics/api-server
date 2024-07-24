package com.projectlyrics.server.domain.note.service;

import com.projectlyrics.server.domain.common.dto.util.CursorBasePaginatedResponse;
import com.projectlyrics.server.domain.favoriteartist.repository.FavoriteArtistQueryRepository;
import com.projectlyrics.server.domain.note.dto.response.NoteGetResponse;
import com.projectlyrics.server.domain.note.repository.NoteQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class NoteQueryService {

    private final NoteQueryRepository noteQueryRepository;
    private final FavoriteArtistQueryRepository favoriteArtistQueryRepository;

    public CursorBasePaginatedResponse<NoteGetResponse> getNotesByUserId(Long userId, Long cursor, int size) {
        Slice<NoteGetResponse> notes = noteQueryRepository.findAllByUserId(userId, cursor, PageRequest.ofSize(size))
                .map(NoteGetResponse::from);

        return CursorBasePaginatedResponse.of(notes);
    }

    public CursorBasePaginatedResponse<NoteGetResponse> getRecentNotes(Long userId, Long cursor, int size) {
        List<Long> artistsIds = favoriteArtistQueryRepository.findAllByUserIdFetchArtist(userId)
                .stream()
                .map(favoriteArtist -> favoriteArtist.getArtist().getId())
                .toList();

        Slice<NoteGetResponse> notes = noteQueryRepository.findAllByArtistIds(artistsIds, cursor, PageRequest.ofSize(size))
                .map(NoteGetResponse::from);

        return CursorBasePaginatedResponse.of(notes);
    }
}
