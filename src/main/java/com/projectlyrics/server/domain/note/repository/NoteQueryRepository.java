package com.projectlyrics.server.domain.note.repository;

import com.projectlyrics.server.domain.note.entity.Note;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface NoteQueryRepository {
    Slice<Note> findAllByUserId(Long userId, Long cursorId, Pageable pageable);

    Slice<Note> findAllByArtistIds(List<Long> artistsIds, Long cursorId, Pageable pageable);
}
