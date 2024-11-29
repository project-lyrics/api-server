package com.projectlyrics.server.global.dev.cron;

import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.artist.repository.ArtistQueryRepository;
import com.projectlyrics.server.domain.song.entity.Song;
import com.projectlyrics.server.domain.song.repository.SongQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class SongCollector {

    private final ArtistQueryRepository artistQueryRepository;
    private final SongQueryRepository songQueryRepository;

    public void collect() {

    }

    public List<Artist> getArtists() {
        return artistQueryRepository.findAll();
    }
}
