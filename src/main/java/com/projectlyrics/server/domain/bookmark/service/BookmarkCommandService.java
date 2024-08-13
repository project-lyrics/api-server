package com.projectlyrics.server.domain.bookmark.service;

import com.projectlyrics.server.domain.bookmark.domain.Bookmark;
import com.projectlyrics.server.domain.bookmark.domain.BookmarkCreate;
import com.projectlyrics.server.domain.bookmark.exception.BookmarkAlreadyExistsException;
import com.projectlyrics.server.domain.bookmark.exception.BookmarkNotFoundException;
import com.projectlyrics.server.domain.bookmark.repository.BookmarkCommandRepository;
import com.projectlyrics.server.domain.bookmark.repository.BookmarkQueryRepository;
import com.projectlyrics.server.domain.note.entity.Note;
import com.projectlyrics.server.domain.note.exception.NoteNotFoundException;
import com.projectlyrics.server.domain.note.repository.NoteQueryRepository;
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
public class BookmarkCommandService {

    private final BookmarkCommandRepository bookmarkCommandRepository;
    private final BookmarkQueryRepository bookmarkQueryRepository;
    private final UserQueryRepository userQueryRepository;
    private final NoteQueryRepository noteQueryRepository;

    public Bookmark create(Long noteId, Long userId) {
        User user = userQueryRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        Note note = noteQueryRepository.findById(noteId)
                .orElseThrow(NoteNotFoundException::new);

        checkIfBookmarkExists(noteId, userId);

        return bookmarkCommandRepository.save(Bookmark.create(BookmarkCreate.of(user, note)));
    }

    private void checkIfBookmarkExists(Long noteId, Long userId) {
        bookmarkQueryRepository.findByNoteIdAndUserId(noteId, userId)
                .ifPresent(bookmark -> { throw new BookmarkAlreadyExistsException(); });
    }

    public void delete(Long noteId, Long userId) {
        bookmarkQueryRepository.findByNoteIdAndUserId(noteId, userId)
                .ifPresentOrElse(
                        bookmark -> bookmark.delete(userId, Clock.systemDefaultZone()),
                        () -> { throw new BookmarkNotFoundException(); }
                );
    }
}
