package com.projectlyrics.server.domain.note.repository;

import com.projectlyrics.server.domain.note.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteCommandRepository extends JpaRepository<Note, Long> {

    void deleteAllByPublisherId(Long publisherId);
}
