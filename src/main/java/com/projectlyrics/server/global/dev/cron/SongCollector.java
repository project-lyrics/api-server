package com.projectlyrics.server.global.dev.cron;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.artist.repository.ArtistQueryRepository;
import com.projectlyrics.server.domain.song.entity.Song;
import com.projectlyrics.server.domain.song.repository.SongCommandRepository;
import com.projectlyrics.server.domain.song.repository.SongQueryRepository;
import com.projectlyrics.server.global.dev.cron.dto.Album;
import com.projectlyrics.server.global.dev.cron.dto.AlbumListResponse;
import com.projectlyrics.server.global.dev.cron.dto.Track;
import com.projectlyrics.server.global.dev.cron.dto.TrackListResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Component
public class SongCollector {

    private final ArtistQueryRepository artistQueryRepository;
    private final SongCommandRepository songCommandRepository;
    private final SongQueryRepository songQueryRepository;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public void collect() {
        for (Artist artist : artistQueryRepository.findAll()) {
            List<Song> newSongs = getSongs(artist)
                    .stream()
                    .filter(this::exists)
                    .toList();

            songCommandRepository.saveAll(newSongs);
        }
    }

    private static List<Song> getSongs(Artist artist) {
        HttpResponse<String> response;
        String url = "https://api.spotify.com/v1/artists/" + artist.getSpotifyId() + "/albums?limit=50&market=KR&locale=ko_KR";
        List<Song> songs = new ArrayList<>();
        AlbumListResponse albumListResponse = null;

        try {
            do {
                response = HttpRequestClient.send(url);

                if (isSuccess(response)) {
                    albumListResponse = objectMapper.readValue(response.body(), AlbumListResponse.class);

                    for (Album album : albumListResponse.getItems()) {
                        songs.addAll(
                                getTracks(album).stream()
                                        .map(track -> new Song(album, track, artist))
                                        .toList()
                        );
                    }

                    url = albumListResponse.getNext();
                }
            } while (isFailed(response) || hasNext(albumListResponse));
        } catch (Exception e) {
            log.error("failed to get albums of an artist {} at all", artist.getId());
        }

        return songs;
    }

    private static List<Track> getTracks(Album album) {
        HttpResponse<String> response;
        String url = "https://api.spotify.com/v1/albums/" + album.getId() + "/tracks?limit=50&market=KR&locale=ko_KR";
        List<Track> tracks = new ArrayList<>();
        TrackListResponse trackListResponse = null;

        try {
            do {
                response = HttpRequestClient.send(url);

                if (isSuccess(response)) {
                    trackListResponse = objectMapper.readValue(response.body(), TrackListResponse.class);
                    tracks.addAll(trackListResponse.getItems());
                    url = trackListResponse.getNext();
                }
            } while (isFailed(response) || hasNext(trackListResponse));
        } catch (Exception e) {
            log.error("failed to get songs of an album at all");
        }

        return tracks;
    }

    private static boolean isSuccess(HttpResponse<?> response) {
        return response.statusCode() == HttpStatus.OK.value();
    }

    private static boolean isFailed(HttpResponse<?> response) {
        AccessTokenProvider.change();
        return response.statusCode() != HttpStatus.OK.value();
    }

    private static boolean hasNext(AlbumListResponse response) {
        return Objects.nonNull(response) && Objects.nonNull(response.getNext());
    }

    private static boolean hasNext(TrackListResponse response) {
        return Objects.nonNull(response) && Objects.nonNull(response.getNext());
    }

    private boolean exists(Song song) {
        return songQueryRepository.findBySpotifyId(song.getSpotifyId())
                .isPresent();
    }

    // TODO: 슬랙으로 보내야 함
    private void print(List<Song> songs) {
    }
}
