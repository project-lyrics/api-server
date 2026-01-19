package com.projectlyrics.server.domain.note.service;

import com.projectlyrics.server.domain.comment.domain.Comment;
import com.projectlyrics.server.domain.comment.repository.CommentQueryRepository;
import com.projectlyrics.server.domain.common.dto.util.CursorBasePaginatedResponse;
import com.projectlyrics.server.domain.favoriteartist.repository.FavoriteArtistQueryRepository;
import com.projectlyrics.server.domain.note.dto.response.NoteDetailResponse;
import com.projectlyrics.server.domain.note.dto.response.NoteGetResponse;
import com.projectlyrics.server.domain.note.entity.Note;
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
    private final CommentQueryRepository commentQueryRepository;
    private final FavoriteArtistQueryRepository favoriteArtistQueryRepository;

    public NoteDetailResponse getNoteById(Long noteId, Long userId) {
        Note note = noteQueryRepository.findById(noteId);
        List<Comment> comments = commentQueryRepository.findAllByNoteId(noteId, userId);

        return NoteDetailResponse.of(note, comments, userId);
    }

    public CursorBasePaginatedResponse<NoteGetResponse> getNotesByUserId(boolean hasLyrics, Long artistId, Long userId, Long cursor, int size) {
        Slice<NoteGetResponse> notes = noteQueryRepository.findAllByUserId(hasLyrics, artistId, userId, cursor, PageRequest.ofSize(size))
                .map(note -> NoteGetResponse.of(note, userId));

        return CursorBasePaginatedResponse.of(notes);
    }

    public CursorBasePaginatedResponse<NoteGetResponse> getNotes(boolean hasLyrics, boolean isFavoriteArtistsOnly, Long userId, Long cursor, int size) {
        List<Long> artistsIds = null;
        if (isFavoriteArtistsOnly) {
            artistsIds = favoriteArtistQueryRepository.findAllByUserIdFetchArtist(userId)
                    .stream()
                    .map(favoriteArtist -> favoriteArtist.getArtist().getId())
                    .toList();
        }

        Slice<NoteGetResponse> notes = noteQueryRepository.findAll(hasLyrics, artistsIds, userId, cursor, PageRequest.ofSize(size))
                .map(note -> NoteGetResponse.of(note, userId));

        return CursorBasePaginatedResponse.of(notes);
    }

    public CursorBasePaginatedResponse<NoteGetResponse> getNotesByArtistId(boolean hasLyrics, Long artistId, Long userId, Long cursor, int size) {
        Slice<NoteGetResponse> notes = noteQueryRepository.findAllByArtistId(hasLyrics, artistId, userId, cursor, PageRequest.ofSize(size))
                .map(note -> NoteGetResponse.of(note, userId));

        return CursorBasePaginatedResponse.of(notes);
    }

    public CursorBasePaginatedResponse<NoteGetResponse> getNotesBySongId(boolean hasLyrics, Long songId, Long userId, Long cursor, int size) {
        Slice<NoteGetResponse> notes = noteQueryRepository.findAllBySongId(hasLyrics, songId, userId, cursor, PageRequest.ofSize(size))
                .map(note -> NoteGetResponse.of(note, userId));

        return CursorBasePaginatedResponse.of(notes);
    }

    public CursorBasePaginatedResponse<NoteGetResponse> getBookmarkedNotes(boolean hasLyrics, Long artistId, Long userId, Long cursor, int size) {
        Slice<NoteGetResponse> notes = noteQueryRepository.findAllBookmarkedAndByArtistId(hasLyrics, artistId, userId, cursor, PageRequest.ofSize(size))
                .map(note -> NoteGetResponse.of(note, userId));

        return CursorBasePaginatedResponse.of(notes);
    }
}
