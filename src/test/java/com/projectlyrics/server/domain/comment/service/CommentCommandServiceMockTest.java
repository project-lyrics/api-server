package com.projectlyrics.server.domain.comment.service;

import com.projectlyrics.server.domain.comment.domain.Comment;
import com.projectlyrics.server.domain.comment.dto.request.CommentCreateRequest;
import com.projectlyrics.server.domain.comment.repository.CommentCommandRepository;
import com.projectlyrics.server.domain.comment.repository.CommentQueryRepository;
import com.projectlyrics.server.domain.note.entity.Note;
import com.projectlyrics.server.domain.note.repository.NoteQueryRepository;
import com.projectlyrics.server.domain.notification.domain.event.CommentEvent;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.repository.UserQueryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentCommandServiceMockTest {

    @Mock
    private CommentCommandRepository commentCommandRepository;

    @Mock
    private CommentQueryRepository commentQueryRepository;

    @Mock
    private UserQueryRepository userQueryRepository;

    @Mock
    private NoteQueryRepository noteQueryRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private CommentCommandService commentCommandService;

    @Test
    void 댓글을_저장할_때_이벤트가_발행된다() {
        // given
        CommentCreateRequest request = new CommentCreateRequest("댓글 내용", 1L);
        when(userQueryRepository.findById(anyLong())).thenReturn(Optional.of(mock(User.class)));
        when(noteQueryRepository.findById(anyLong())).thenReturn(Optional.of(mock(Note.class)));
        when(commentCommandRepository.save(any(Comment.class))).thenReturn(mock(Comment.class));

        // when
        commentCommandService.create(request, 1L);

        // then
        verify(eventPublisher).publishEvent(any(CommentEvent.class));
    }
}