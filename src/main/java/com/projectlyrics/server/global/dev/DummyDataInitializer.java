package com.projectlyrics.server.global.dev;

import com.opencsv.CSVReader;
import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.artist.entity.ArtistCreate;
import com.projectlyrics.server.domain.artist.repository.ArtistCommandRepository;
import com.projectlyrics.server.domain.song.entity.Song;
import com.projectlyrics.server.domain.song.entity.SongCreate;
import com.projectlyrics.server.domain.song.repository.SongCommandRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Profile({"dev"})
@Component
@RequiredArgsConstructor
public class DummyDataInitializer {

    private final ArtistCommandRepository artistCommandRepository;
    private final SongCommandRepository songCommandRepository;

    @PostConstruct
    public void init() {
        List<Artist> artists = saveInitialArtists();
        saveInitialSongs(artists);
    }

    private List<Artist> saveInitialArtists() {
        List<Artist> artists = new ArrayList<>();

        try {
            CSVReader reader = new CSVReader(new FileReader("src/main/resources/artists.csv"));
            String[] line;

            while ((line = reader.readNext()) != null) {
                ArtistCreate artistCreate = new ArtistCreate(
                        Long.parseLong(line[0]),
                        line[1],
                        line[2],
                        line[3],
                        line[4],
                        line[5]
                );

                artists.add(Artist.create(artistCreate));
            }
        } catch (Exception e) {
            log.error("failed to read csv file", e);
        }

        return artistCommandRepository.saveAll(artists);
    }

    private void saveInitialSongs(List<Artist> artists) {
        List<Song> songs = new ArrayList<>();

        try {
            CSVReader reader = new CSVReader(new FileReader("src/main/resources/songs.csv"));
            String[] line;

            while ((line = reader.readNext()) != null) {
                SongCreate songCreate = new SongCreate(
                        Long.parseLong(line[0]),
                        line[3],
                        line[1],
                        LocalDate.parse(line[6]),
                        line[5],
                        line[4],
                        findArtist(Long.parseLong(line[2]), artists)
                );

                songs.add(Song.create(songCreate));
            }
        } catch (Exception e) {
            log.error("failed to read csv file", e);
        }

        songCommandRepository.saveAll(songs);
    }

    private Artist findArtist(Long id, List<Artist> artists) {
        return artists.stream()
                .filter(artist -> artist.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
