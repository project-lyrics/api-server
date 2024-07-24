package com.projectlyrics.server.domain.song.service;

import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.artist.exception.ArtistNotFoundException;
import com.projectlyrics.server.domain.artist.repository.ArtistQueryRepository;
import com.projectlyrics.server.domain.song.dto.request.SongCreateRequest;
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
    private final ArtistQueryRepository artistQueryRepository;

    public Song create(SongCreateRequest request) {
        Artist artist = artistQueryRepository.findByIdAndNotDeleted(request.artistId())
                .orElseThrow(ArtistNotFoundException::new);

        return songCommandRepository.save(Song.create(SongCreate.from(request, artist)));
    }
}
