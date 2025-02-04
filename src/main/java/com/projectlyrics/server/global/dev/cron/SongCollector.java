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
import com.projectlyrics.server.global.slack.domain.SlackResponseBuilder;
import com.projectlyrics.server.global.slack.service.SlackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.http.HttpResponse;
import java.time.LocalDateTime;
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
    private final SlackService slackService;

    @Value("${slack.channel.songs_id}")
    private String channelId;

    @Async
    @Scheduled(cron = "0 0 * * * *")
    public void collect() {
        log.info("song collector starts collecting!");
        List<Artist> artists = artistQueryRepository.findAll();

        for (Artist artist : subList(artists)) {
            List<Song> newSongs = getSongs(artist)
                    .stream()
                    .filter(this::notRegistered)
                    .toList();

            songCommandRepository.saveAll(newSongs);
            sendToSlack(newSongs);
        }
    }

    private List<Artist> subList(List<Artist> artists) {
        if (artists.size() <= 23) {
            return artists;
        }
        
        int now = LocalDateTime.now().getHour();
        int size = artists.size() / 23;

        int from = now * size;
        int to = Math.min(from + size, artists.size());

        return artists.subList(from, to);
    }

    private static List<Song> getSongs(Artist artist) {
        HttpResponse<String> response;
        String url = "https://api.spotify.com/v1/artists/" + artist.getSpotifyId() + "/albums?limit=50&market=KR&locale=ko_KR&include_groups=album,single";
        List<Song> songs = new ArrayList<>();
        AlbumListResponse albumListResponse = null;

        log.info("songs of {} is being collected", artist.getName());
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
            log.error("failed to get albums of an artist {} of id {} at all", artist.getName(), artist.getId());
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
            log.error("failed to get songs of an album {} of artist {} of id {} at all", album.getName(), album.getId(), album.getId());
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

    private boolean notRegistered(Song song) {
        return songQueryRepository.findBySpotifyId(song.getSpotifyId())
                .isEmpty();
    }

    private void sendToSlack(List<Song> songs) {
        songs.forEach(song ->
            slackService.sendFeedbackToSlack(SlackResponseBuilder.createSong(song), null, channelId));
    }
}
