package com.projectlyrics.server.domain.song.search;

import com.projectlyrics.server.domain.artist.repository.ArtistCommandRepository;
import com.projectlyrics.server.domain.song.entity.Song;
import com.projectlyrics.server.domain.song.entity.SongMongo;
import com.projectlyrics.server.domain.song.repository.SongCommandRepository;
import com.projectlyrics.server.domain.song.repository.SongMongoCommandRepository;
import com.projectlyrics.server.domain.song.repository.SongMongoQueryRepository;
import com.projectlyrics.server.domain.song.repository.SongQueryRepository;
import com.projectlyrics.server.domain.song.service.SongQueryService;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("local")
@Disabled("성능 측정용이라 기본 실행에서 제외함")
class SongSearchPerformanceTest {

    @Autowired
    private SongMongoCommandRepository songMongoCommandRepository;

    @Autowired
    private SongMongoQueryRepository songMongoQueryRepository;

    @Autowired
    private ArtistCommandRepository artistCommandRepository;

    @Autowired
    private SongCommandRepository songCommandRepository;

    @Autowired
    private SongQueryRepository songQueryRepository;

    @Autowired
    private SongQueryService songQueryService;


    @BeforeEach
    void setup() {
        songMongoCommandRepository.deleteAll();

        long startBulk = System.currentTimeMillis();
        List<Song> existingSongs = songQueryRepository.findAll();
        List<SongMongo> mongoBatch = existingSongs.stream()
                .map(SongMongo::of)
                .toList();

        songMongoCommandRepository.saveAll(mongoBatch);

        long endBulk = System.currentTimeMillis();
        System.out.println("Bulk insert " + mongoBatch.size() + "개 걸린 시간: " + (endBulk - startBulk) + " ms");
    }


    @Test
    void searchPerformanceTest() {
        List<String> keywords = List.of(
                "Flying", "Bobs", "불세례", "환상", "용맹", "발걸음",
                "오랜만", "마지막", "black", "eres", "ai", "기사",
                "Kidd", "joke", "Everything", "Pet", "Kite", "War",
                "Harmony", "네 번", "여름", "21st", "Century", "Kingdom",
                "Hollywood", "Movie", "Star", "Hope", "결국", "울었어요",
                "NAIVE", "테니스", "몰라요", "우주선", "좋은 사람", "속물들",
                "마술", "수성", "하루", "Rope", "What", "Say", "귀가",
                "ㅈㅣㅂ", "어제", "당신", "꿈", "Lovegame", "lonely",
                "정말", "오래", "마음", "스물하나", "열다섯", "구름", "날아",
                "bad", "sunny", "Knowhow", "MP3", "Love is Dangerous", "Bully",
                "네가 사랑한", "Instant", "Lover", "Melancholy", "말야", "사랑해",
                "외로워", "푸훗", "Poo", "Hut", "동경", "인사", "도미노",
                "아니어도", "이상해", "Rule", "Astronaut", "살고 싶어요", "나도",
                "꿈에서 걸려온 전화", "이름이 없는", "유령", "save me", "Madeleine", "Love",
                "Weather", "Report", "잠깐", "잠들어", "내려앉는", "숨", "우주를 줄게",
                "싸운날", "Go Your Way", "Bourgeois", "Emotion", "100번째", "같은 말",
                "좋아한다고", "옥상달빛", "무사히", "도착", "생각해봐", "Sihanoukville", "나의 왼발",
                "Walking", "with you", "Instrumental", "이름이 맘에", "Stopwatch", "사랑이 악역을",
                "김사월", "악역의 등장", "여울", "달", "Joy", "despair", "의자에 앉아",
                "상견니", "얼굴", "변치 말자", "내 꿈은", "너무해", "Toss", "turn", "들불",
                "구운듯한", "모티프", "목에게"
        );

        // 각 케이스별 latency(ms) 저장
        List<Long> mysqlLikeServiceLatencies = new java.util.ArrayList<>();
        List<Long> mongoAtlasServiceLatencies = new java.util.ArrayList<>();
        List<Long> mysqlLikeRepoLatencies = new java.util.ArrayList<>();
        List<Long> mongoAtlasRepoLatencies = new java.util.ArrayList<>();

        // MySQL LIKE 테스트 - service
        for (String keyword : keywords) {
            long start = System.nanoTime();
            songQueryService.searchSongsWithLike(keyword, PageRequest.of(0, 12));
            mysqlLikeServiceLatencies.add((System.nanoTime() - start) / 1_000_000);
        }

        // Mongo Atlas Search 테스트 - service
        for (String keyword : keywords) {
            long start = System.nanoTime();
            songQueryService.searchSongs(keyword, 0, 12);
            mongoAtlasServiceLatencies.add((System.nanoTime() - start) / 1_000_000);
        }

        // MySQL LIKE 테스트 - repository
        for (String keyword : keywords) {
            long start = System.nanoTime();
            songQueryRepository.findAllByQuery(keyword, PageRequest.of(0, 5));
            mysqlLikeRepoLatencies.add((System.nanoTime() - start) / 1_000_000);
        }

        // Mongo Atlas Search 테스트 - repository
        for (String keyword : keywords) {
            long start = System.nanoTime();
            songMongoQueryRepository.searchSongsByName(keyword, 0, 12);
            mongoAtlasRepoLatencies.add((System.nanoTime() - start) / 1_000_000);
        }

        java.util.function.Function<List<Long>, String> stats = (latencies) -> {
            latencies.sort(Long::compare);
            double avg = latencies.stream().mapToLong(Long::longValue).average().orElse(0.0);
            long p99 = latencies.get((int) Math.ceil(latencies.size() * 0.99) - 1);
            return String.format("평균=%.3f ms, P99=%d ms", avg, p99);
        };

        System.out.println("MySQL LIKE Repository -> " + stats.apply(mysqlLikeRepoLatencies));
        System.out.println("Mongo Atlas Repository -> " + stats.apply(mongoAtlasRepoLatencies));
        System.out.println("MySQL LIKE Service -> " + stats.apply(mysqlLikeServiceLatencies));
        System.out.println("Mongo Atlas Service -> " + stats.apply(mongoAtlasServiceLatencies));
    }

    @AfterEach
    void cleanup() {
        songMongoCommandRepository.deleteAll();
    }
}
