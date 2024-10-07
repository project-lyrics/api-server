package com.projectlyrics.server.domain.comment.service;

import com.projectlyrics.server.domain.comment.domain.Comment;
import com.projectlyrics.server.domain.comment.domain.CommentCreate;
import com.projectlyrics.server.domain.comment.domain.CommentUpdate;
import com.projectlyrics.server.domain.comment.dto.request.CommentCreateRequest;
import com.projectlyrics.server.domain.comment.dto.request.CommentUpdateRequest;
import com.projectlyrics.server.domain.comment.exception.InvalidCommentDeletionException;
import com.projectlyrics.server.domain.comment.exception.InvalidCommentUpdateException;
import com.projectlyrics.server.domain.comment.repository.CommentCommandRepository;
import com.projectlyrics.server.domain.comment.repository.CommentQueryRepository;
import com.projectlyrics.server.domain.discipline.exception.InvaildDisciplineActionException;
import com.projectlyrics.server.domain.discipline.repository.DisciplineQueryRepository;
import com.projectlyrics.server.domain.note.entity.Note;
import com.projectlyrics.server.domain.note.exception.NoteNotFoundException;
import com.projectlyrics.server.domain.note.repository.NoteQueryRepository;
import com.projectlyrics.server.domain.notification.domain.event.CommentEvent;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.exception.UserNotFoundException;
import com.projectlyrics.server.domain.user.repository.UserQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentCommandService {

    private final CommentCommandRepository commentCommandRepository;
    private final CommentQueryRepository commentQueryRepository;
    private final UserQueryRepository userQueryRepository;
    private final NoteQueryRepository noteQueryRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final DisciplineQueryRepository disciplineQueryRepository;

    public Comment create(CommentCreateRequest request, Long writerId) {
        User writer = userQueryRepository.findById(writerId)
                .orElseThrow(UserNotFoundException::new);
        Note note = noteQueryRepository.findById(request.noteId())
                .orElseThrow(NoteNotFoundException::new);

        checkDiscipline(note.getSong().getArtist().getId(), writerId);

        Comment comment = Comment.create(CommentCreate.of(request.content(), writer, note));
        eventPublisher.publishEvent(CommentEvent.from(comment));
        return commentCommandRepository.save(comment);
    }

    public Comment update(CommentUpdateRequest request, Long commentId, Long writerId) {
        Comment comment = commentQueryRepository.findById(commentId)
                .filter(foundComment -> foundComment.isWriter(writerId))
                .orElseThrow(InvalidCommentUpdateException::new);

        return comment.update(CommentUpdate.from(request));
    }

    public void delete(Long commentId, Long writerId) {
        commentQueryRepository.findById(commentId)
                .filter(comment -> comment.isWriter(writerId))
                .ifPresentOrElse(
                        comment -> comment.delete(writerId, Clock.systemDefaultZone()),
                        () -> { throw new InvalidCommentDeletionException(); }
                );
    }

    private void checkDiscipline(Long artistId, Long userId) {
        if (disciplineQueryRepository.existsDisciplineOfAll(userId) || disciplineQueryRepository.existsDisciplineOfArtist(artistId, userId)) {
            throw new InvaildDisciplineActionException();
        }
    }
}
