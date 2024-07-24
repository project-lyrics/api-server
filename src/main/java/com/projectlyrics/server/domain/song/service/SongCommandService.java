package com.projectlyrics.server.domain.song.service;

import com.projectlyrics.server.domain.song.entity.Song;
import com.projectlyrics.server.domain.song.entity.SongCreate;
import com.projectlyrics.server.domain.song.repository.SongCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class SongCommandService {

    private final SongCommandRepository songCommandRepository;

    public Song create(SongCreate songCreate) {
        return songCommandRepository.save(Song.create(songCreate));
    }
}
