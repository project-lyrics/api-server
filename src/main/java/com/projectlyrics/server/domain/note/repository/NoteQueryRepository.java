package com.projectlyrics.server.domain.note.repository;

import com.projectlyrics.server.domain.note.entity.Note;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Optional;

public interface NoteQueryRepository {
    Optional<Note> findById(Long id);

    Slice<Note> findAllByUserId(Long userId, Long cursorId, Pageable pageable);

    Slice<Note> findAllByArtistIds(List<Long> artistsIds, Long cursorId, Pageable pageable);

    Slice<Note> findAllByArtistId(Long artistId, Long cursorId, Pageable pageable);

    Slice<Note> findAllByArtistIdAndHasLyrics(Long artistId, Long cursorId, Pageable pageable);

    long countDraftNotesByUserId(Long userId);
}
