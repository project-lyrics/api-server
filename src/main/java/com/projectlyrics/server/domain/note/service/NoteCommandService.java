package com.projectlyrics.server.domain.note.service;

import com.projectlyrics.server.domain.note.dto.request.NoteCreateRequest;
import com.projectlyrics.server.domain.note.dto.request.NoteUpdateRequest;
import com.projectlyrics.server.domain.note.entity.Note;
import com.projectlyrics.server.domain.note.entity.NoteCreate;
import com.projectlyrics.server.domain.note.entity.NoteUpdate;
import com.projectlyrics.server.domain.note.exception.InvalidNoteDeletionException;
import com.projectlyrics.server.domain.note.exception.InvalidNoteUpdateException;
import com.projectlyrics.server.domain.note.repository.NoteCommandRepository;
import com.projectlyrics.server.domain.note.repository.NoteQueryRepository;
import com.projectlyrics.server.domain.song.entity.Song;
import com.projectlyrics.server.domain.song.exception.SongNotFoundException;
import com.projectlyrics.server.domain.song.repository.SongQueryRepository;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.exception.UserNotFoundException;
import com.projectlyrics.server.domain.user.repository.UserQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class NoteCommandService {

    private final NoteCommandRepository noteCommandRepository;
    private final NoteQueryRepository noteQueryRepository;
    private final UserQueryRepository userQueryRepository;
    private final SongQueryRepository songQueryRepository;

    public Note create(NoteCreateRequest request) {
        User publisher = userQueryRepository.findById(request.publisherId())
                .orElseThrow(UserNotFoundException::new);
        Song song = songQueryRepository.findById(request.songId())
                .orElseThrow(SongNotFoundException::new);

        return noteCommandRepository.save(Note.create(NoteCreate.from(request, publisher, song)));
    }

    public void delete(long publisherId, long noteId) {
        noteQueryRepository.findById(noteId)
                .filter(note -> note.isPublisher(publisherId))
                .ifPresentOrElse(
                        noteCommandRepository::delete,
                        () -> { throw new InvalidNoteDeletionException(); });
    }

    public Note update(NoteUpdateRequest request) {
        Note note = noteQueryRepository.findById(request.noteId())
                .filter(foundNote -> foundNote.isPublisher(request.publisherId()))
                .orElseThrow(InvalidNoteUpdateException::new);

        return note.update(NoteUpdate.from(request));
    }
}
