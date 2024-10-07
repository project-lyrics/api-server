package com.projectlyrics.server.domain.note.service;

import com.projectlyrics.server.domain.discipline.exception.InvaildDisciplineActionException;
import com.projectlyrics.server.domain.discipline.repository.DisciplineQueryRepository;
import com.projectlyrics.server.domain.note.dto.request.NoteCreateRequest;
import com.projectlyrics.server.domain.note.dto.request.NoteUpdateRequest;
import com.projectlyrics.server.domain.note.entity.Note;
import com.projectlyrics.server.domain.note.entity.NoteCreate;
import com.projectlyrics.server.domain.note.entity.NoteStatus;
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

import java.time.Clock;

@Service
@Transactional
@RequiredArgsConstructor
public class NoteCommandService {

    private final NoteCommandRepository noteCommandRepository;
    private final NoteQueryRepository noteQueryRepository;
    private final UserQueryRepository userQueryRepository;
    private final SongQueryRepository songQueryRepository;
    private final DisciplineQueryRepository disciplineQueryRepository;

    public Note create(NoteCreateRequest request, Long publisherId) {
        User publisher = userQueryRepository.findById(publisherId)
                .orElseThrow(UserNotFoundException::new);
        Song song = songQueryRepository.findById(request.songId())
                .orElseThrow(SongNotFoundException::new);

        checkDiscipline(song.getArtist().getId(), publisherId);

        if (request.status().equals(NoteStatus.DRAFT)) {
            checkDrafts(publisher.getId());
        }
        return noteCommandRepository.save(Note.create(NoteCreate.from(request, publisher, song)));
    }

    private void checkDiscipline(Long artistId, Long userId) {
       if (disciplineQueryRepository.existsDisciplineOfAll(userId) || disciplineQueryRepository.existsDisciplineOfArtist(artistId, userId)) {
           throw new InvaildDisciplineActionException();
       }
    }

    private void checkDrafts(long publisherId) {
        Note.checkDraftNumber(noteQueryRepository.countDraftNotesByUserId(publisherId));
    }

    public void delete(long publisherId, long noteId) {
        noteQueryRepository.findById(noteId)
                .filter(note -> note.isPublisher(publisherId))
                .ifPresentOrElse(
                        note -> note.delete(publisherId, Clock.systemDefaultZone()),
                        () -> { throw new InvalidNoteDeletionException(); });
    }

    public Note update(NoteUpdateRequest request, Long noteId, Long publisherId) {
        Note note = noteQueryRepository.findById(noteId)
                .filter(foundNote -> foundNote.isPublisher(publisherId))
                .orElseThrow(InvalidNoteUpdateException::new);

        return note.update(NoteUpdate.from(request));
    }
}
