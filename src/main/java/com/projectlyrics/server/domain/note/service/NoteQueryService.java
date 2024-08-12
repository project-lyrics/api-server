package com.projectlyrics.server.domain.note.service;

import com.projectlyrics.server.domain.common.dto.util.CursorBasePaginatedResponse;
import com.projectlyrics.server.domain.favoriteartist.repository.FavoriteArtistQueryRepository;
import com.projectlyrics.server.domain.note.dto.response.NoteDetailResponse;
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

    public NoteDetailResponse getNoteById(Long userId, Long noteId) {
        return noteQueryRepository.findById(noteId)
                .map(note -> NoteDetailResponse.of(note, note.getComments(), userId))
                .orElseThrow();
    }

    public CursorBasePaginatedResponse<NoteGetResponse> getNotesByUserId(Long userId, Long cursor, int size) {
        Slice<NoteGetResponse> notes = noteQueryRepository.findAllByUserId(userId, cursor, PageRequest.ofSize(size))
                .map(note -> NoteGetResponse.of(note, userId));

        return CursorBasePaginatedResponse.of(notes);
    }

    public CursorBasePaginatedResponse<NoteGetResponse> getNotesOfFavoriteArtists(Long userId, Long cursor, int size) {
        List<Long> artistsIds = favoriteArtistQueryRepository.findAllByUserIdFetchArtist(userId)
                .stream()
                .map(favoriteArtist -> favoriteArtist.getArtist().getId())
                .toList();

        Slice<NoteGetResponse> notes = noteQueryRepository.findAllByArtistIds(artistsIds, cursor, PageRequest.ofSize(size))
                .map(note -> NoteGetResponse.of(note, userId));

        return CursorBasePaginatedResponse.of(notes);
    }

    public CursorBasePaginatedResponse<NoteGetResponse> getNotesByArtistId(Long userId, Long artistId, boolean hasLyrics, Long cursor, int size) {
        if (hasLyrics) {
            return getNotesByArtistIdWithLyrics(artistId, userId, cursor, size);
        }

        return getNotesByArtistIdWithoutLyrics(artistId, userId, cursor, size);
    }

    private CursorBasePaginatedResponse<NoteGetResponse> getNotesByArtistIdWithLyrics(Long artistId, Long userId, Long cursor, int size) {
        Slice<NoteGetResponse> notes = noteQueryRepository.findAllByArtistIdAndHasLyrics(artistId, cursor, PageRequest.ofSize(size))
                .map(note -> NoteGetResponse.of(note, userId));

        return CursorBasePaginatedResponse.of(notes);
    }

    private CursorBasePaginatedResponse<NoteGetResponse> getNotesByArtistIdWithoutLyrics(Long artistId, Long userId, Long cursor, int size) {
        Slice<NoteGetResponse> notes = noteQueryRepository.findAllByArtistId(artistId, cursor, PageRequest.ofSize(size))
                .map(note -> NoteGetResponse.of(note, userId));

        return CursorBasePaginatedResponse.of(notes);
    }
}
