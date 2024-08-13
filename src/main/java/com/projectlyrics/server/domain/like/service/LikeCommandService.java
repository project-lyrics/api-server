package com.projectlyrics.server.domain.like.service;

import com.projectlyrics.server.domain.like.domain.Like;
import com.projectlyrics.server.domain.like.domain.LikeCreate;
import com.projectlyrics.server.domain.like.exception.LikeNotFoundException;
import com.projectlyrics.server.domain.like.repository.LikeCommandRepository;
import com.projectlyrics.server.domain.like.repository.LikeQueryRepository;
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
public class LikeCommandService {

    private final LikeCommandRepository likeCommandRepository;
    private final LikeQueryRepository likeQueryRepository;
    private final UserQueryRepository userQueryRepository;
    private final NoteQueryRepository noteQueryRepository;

    public Like create(Long noteId, Long userId) {
        User user = userQueryRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        Note note = noteQueryRepository.findById(noteId)
                .orElseThrow(NoteNotFoundException::new);

        return likeCommandRepository.save(Like.create(LikeCreate.of(user, note)));
    }

    public void delete(Long noteId, Long userId) {
        likeQueryRepository.findByNoteIdAndUserId(noteId, userId)
                .ifPresentOrElse(
                        like -> like.delete(userId, Clock.systemDefaultZone()),
                        () -> { throw new LikeNotFoundException(); }
                );
    }
}
