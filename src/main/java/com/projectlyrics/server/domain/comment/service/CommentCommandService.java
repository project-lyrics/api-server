package com.projectlyrics.server.domain.comment.service;

import com.projectlyrics.server.domain.comment.domain.Comment;
import com.projectlyrics.server.domain.comment.domain.CommentCreate;
import com.projectlyrics.server.domain.comment.dto.request.CommentCreateRequest;
import com.projectlyrics.server.domain.comment.repository.CommentCommandRepository;
import com.projectlyrics.server.domain.note.entity.Note;
import com.projectlyrics.server.domain.note.exception.NoteNotFoundException;
import com.projectlyrics.server.domain.note.repository.NoteQueryRepository;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.exception.UserNotFoundException;
import com.projectlyrics.server.domain.user.repository.UserQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentCommandService {

    private final CommentCommandRepository commentCommandRepository;
    private final UserQueryRepository userQueryRepository;
    private final NoteQueryRepository noteQueryRepository;

    public Comment create(CommentCreateRequest request, Long writerId) {
        User writer = userQueryRepository.findById(writerId)
                .orElseThrow(UserNotFoundException::new);
        Note note = noteQueryRepository.findById(request.noteId())
                .orElseThrow(NoteNotFoundException::new);

        return commentCommandRepository.save(Comment.create(CommentCreate.of(request.content(), writer, note)));
    }
}
