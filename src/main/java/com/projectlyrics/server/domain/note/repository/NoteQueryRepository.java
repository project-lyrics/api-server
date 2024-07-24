package com.projectlyrics.server.domain.note.repository;

import com.projectlyrics.server.domain.note.entity.Note;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface NoteQueryRepository {
    Slice<Note> findAllByUserId(Long userId, Long cursorId, Pageable pageable);
}
