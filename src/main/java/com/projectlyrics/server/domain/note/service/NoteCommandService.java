package com.projectlyrics.server.domain.note.service;

import com.projectlyrics.server.domain.discipline.exception.InvalidDisciplineActionException;
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
import java.time.Clock;
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
    private final DisciplineQueryRepository disciplineQueryRepository;

    public Note create(NoteCreateRequest request, Long publisherId) {
        User publisher = userQueryRepository.findById(publisherId)
                .orElseThrow(UserNotFoundException::new);
        Song song = songQueryRepository.findById(request.songId())
                .orElseThrow(SongNotFoundException::new);

        System.out.println("----------------------------");
        System.out.println("song.artist() = " + song.getArtist());
        System.out.println("song.artist().id() = " + song.getArtist().getId());
        System.out.println("song.artist().name() = " + song.getArtist().getName());
        System.out.println("song.artist().imageUrl() = " + song.getArtist().getImageUrl());
        System.out.println("----------------------------");

        checkDiscipline(song.getArtist().getId(), publisherId);

        if (request.status().equals(NoteStatus.DRAFT)) {
            checkDrafts(publisher.getId());
        }
        return noteCommandRepository.save(Note.create(NoteCreate.from(request, publisher, new Song(song.getId()))));
    }

    private void checkDiscipline(Long artistId, Long userId) {
       if (disciplineQueryRepository.existsByAll(userId) || disciplineQueryRepository.existsByArtist(artistId, userId)) {
           throw new InvalidDisciplineActionException();
       }
    }

    private void checkDrafts(long publisherId) {
        Note.checkDraftNumber(noteQueryRepository.countDraftNotesByUserId(publisherId));
    }

    public void delete(long publisherId, long noteId) {
        Note note = noteQueryRepository.findById(noteId);

        if (note.isPublisher(publisherId)) {
            note.delete(publisherId, Clock.systemDefaultZone());
        } else {
            throw new InvalidNoteDeletionException();
        }
    }

    public Note update(NoteUpdateRequest request, Long noteId, Long publisherId) {
        Note note = noteQueryRepository.findById(noteId);

        if (note.isPublisher(publisherId)) {
            return note.update(NoteUpdate.from(request));
        } else {
            throw new InvalidNoteUpdateException();
        }
    }
}
