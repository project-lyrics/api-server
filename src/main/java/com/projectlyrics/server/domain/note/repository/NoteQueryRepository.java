package com.projectlyrics.server.domain.note.repository;

import com.projectlyrics.server.domain.note.entity.Note;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Optional;

public interface NoteQueryRepository {

    Optional<Note> findById(Long id);
    Optional<Note> findById(Long id, Long userId);

    Slice<Note> findAllByUserId(boolean hasLyrics, Long artistId, Long userId, Long cursorId, Pageable pageable);
    Slice<Note> findAllByArtistIds(boolean hasLyrics, List<Long> artistsIds, Long userId, Long cursorId, Pageable pageable);
    Slice<Note> findAllByArtistId(boolean hasLyrics, Long artistId, Long userId, Long cursorId, Pageable pageable);
    Slice<Note> findAllBookmarkedAndByArtistId(boolean hasLyrics, Long artistId, Long userId, Long cursorId, Pageable pageable);
    Slice<Note> findAllBySongId(boolean hasLyrics, Long songId, Long userId, Long cursorId, Pageable pageable);

    long countDraftNotesByUserId(Long userId);
}
