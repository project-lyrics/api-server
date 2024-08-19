package com.projectlyrics.server.domain.note.service;

import com.projectlyrics.server.domain.common.dto.util.CursorBasePaginatedResponse;
import com.projectlyrics.server.domain.favoriteartist.repository.FavoriteArtistQueryRepository;
import com.projectlyrics.server.domain.note.dto.response.NoteDetailResponse;
import com.projectlyrics.server.domain.note.dto.response.NoteGetResponse;
import com.projectlyrics.server.domain.note.entity.Note;
import com.projectlyrics.server.domain.note.repository.NoteQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
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

    public CursorBasePaginatedResponse<NoteGetResponse> getNotesByUserId(boolean hasLyrics, Long userId, Long cursor, int size) {
        Slice<NoteGetResponse> notes = noteQueryRepository.findAllByUserId(hasLyrics, userId, cursor, PageRequest.ofSize(size))
                .map(note -> NoteGetResponse.of(note, userId));

        return CursorBasePaginatedResponse.of(notes);
    }

    public CursorBasePaginatedResponse<NoteGetResponse> getNotesOfFavoriteArtists(boolean hasLyrics, Long userId, Long cursor, int size) {
        List<Long> artistsIds = favoriteArtistQueryRepository.findAllByUserIdFetchArtist(userId)
                .stream()
                .map(favoriteArtist -> favoriteArtist.getArtist().getId())
                .toList();

        Slice<NoteGetResponse> notes = noteQueryRepository.findAllByArtistIds(hasLyrics, artistsIds, cursor, PageRequest.ofSize(size))
                .map(note -> NoteGetResponse.of(note, userId));

        return CursorBasePaginatedResponse.of(notes);
    }

    public CursorBasePaginatedResponse<NoteGetResponse> getNotesByArtistId(boolean hasLyrics, Long userId, Long artistId, Long cursor, int size) {
        Slice<NoteGetResponse> notes = noteQueryRepository.findAllByArtistId(hasLyrics, artistId, cursor, PageRequest.ofSize(size))
                .map(note -> NoteGetResponse.of(note, userId));

        return CursorBasePaginatedResponse.of(notes);
    }

    public CursorBasePaginatedResponse<NoteGetResponse> getBookmarkedNotes(boolean hasLyrics, Long artistId, Long userId, Long cursor, int size) {
        Slice<Note> noteSlice = noteQueryRepository.findAllByUserId(hasLyrics, userId, cursor, PageRequest.ofSize(size));

        List<NoteGetResponse> content = noteSlice.stream()
                .filter(note -> note.isBookmarked(userId) && note.isAssociatedWithArtist(artistId))
                .map(note -> NoteGetResponse.of(note, userId))
                .toList();

        return CursorBasePaginatedResponse.of(new SliceImpl<>(content, noteSlice.getPageable(), noteSlice.hasNext()));
    }
}
