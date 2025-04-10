package com.projectlyrics.server.domain.view.service;

import com.projectlyrics.server.domain.note.entity.Note;
import com.projectlyrics.server.domain.note.repository.NoteQueryRepository;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.exception.UserNotFoundException;
import com.projectlyrics.server.domain.user.repository.UserQueryRepository;
import com.projectlyrics.server.domain.view.domain.View;
import com.projectlyrics.server.domain.view.domain.ViewCreate;
import com.projectlyrics.server.domain.view.repository.ViewCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ViewCommandService {

    private final ViewCommandRepository viewCommandRepository;
    private final NoteQueryRepository noteQueryRepository;
    private final UserQueryRepository userQueryRepository;

    public View create(Long noteId, Long userId, String deviceId) {
        Note note = noteQueryRepository.findById(noteId);
        User user = userQueryRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        return viewCommandRepository.save(View.create(ViewCreate.of(note, user, deviceId)));
    }

    public View create(Long noteId, String deviceId) {
        Note note = noteQueryRepository.findById(noteId);
        return viewCommandRepository.save(View.create(ViewCreate.of(note, null, deviceId)));
    }
}
