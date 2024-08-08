package com.projectlyrics.server.domain.like.service;

import com.projectlyrics.server.domain.like.repository.LikeQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class LikeQueryService {

    private final LikeQueryRepository likeQueryRepository;

    public long countLikesOfNote(Long noteId) {
        return likeQueryRepository.countByNoteId(noteId);
    }
}
