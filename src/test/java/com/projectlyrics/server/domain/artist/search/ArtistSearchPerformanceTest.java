package com.projectlyrics.server.domain.artist.search;

import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.artist.entity.ArtistMongo;
import com.projectlyrics.server.domain.artist.repository.ArtistCommandRepository;
import com.projectlyrics.server.domain.artist.repository.ArtistMongoCommandRepository;
import com.projectlyrics.server.domain.artist.repository.ArtistMongoQueryRepository;
import com.projectlyrics.server.domain.artist.repository.ArtistQueryRepository;
import com.projectlyrics.server.domain.artist.service.ArtistQueryService;
import com.projectlyrics.server.support.fixture.ArtistFixture;
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
class ArtistSearchPerformanceTest {

    @Autowired
    private ArtistMongoCommandRepository artistMongoCommandRepository;

    @Autowired
    private ArtistMongoQueryRepository artistMongoQueryRepository;

    @Autowired
    private ArtistCommandRepository artistCommandRepository;

    @Autowired
    private ArtistQueryRepository artistQueryRepository;

    @Autowired
    private ArtistQueryService artistQueryService;

    private final List<Artist> templates = List.of(
            ArtistFixture.create("검정치마", "", ""), ArtistFixture.create("잔나비", "", ""),
            ArtistFixture.create("최유리", "", ""), ArtistFixture.create("실리카겔", "Silica Gel", ""),
            ArtistFixture.create("데이먼스 이어", "Damons year", ""), ArtistFixture.create("새소년", "", ""),
            ArtistFixture.create("10CM", "십센치", ""), ArtistFixture.create("혁오", "HYUKOH", ""),
            ArtistFixture.create("카더가든", "car the garden", ""), ArtistFixture.create("너드커넥션", "Nerd Connection", ""),
            ArtistFixture.create("허회경", "", ""), ArtistFixture.create("우효", "OOHYO", ""),
            ArtistFixture.create("백아", "Baek A", ""), ArtistFixture.create("브로콜리너마저", "Broccoli you too", ""),
            ArtistFixture.create("쏜애플", "THORNAPPLE", ""), ArtistFixture.create("SURL", "설", ""),
            ArtistFixture.create("한로로", "HANRORO", ""), ArtistFixture.create("윤지영", "", ""),
            ArtistFixture.create("백예린", "Yerin Baek", ""), ArtistFixture.create("유다빈밴드", "YdBB", ""),
            ArtistFixture.create("신지훈", "", ""), ArtistFixture.create("wave to earth", "웨이브투어스", ""),
            ArtistFixture.create("LUCY", "루시", ""), ArtistFixture.create("터치드", "TOUCHED", ""),
            ArtistFixture.create("하현상", "", ""), ArtistFixture.create("김사월", "", ""),
            ArtistFixture.create("언니네 이발관", "", ""), ArtistFixture.create("나상현씨밴드", "Band Nah", ""),
            ArtistFixture.create("박소은", "", ""), ArtistFixture.create("짙은", "", ""),
            ArtistFixture.create("Mingginyu", "밍기뉴", ""), ArtistFixture.create("김뜻돌", "", ""),
            ArtistFixture.create("Lacuna", "라쿠나", ""), ArtistFixture.create("CHEEZE", "치즈", ""),
            ArtistFixture.create("김현창", "", ""), ArtistFixture.create("볼빨간사춘기", "", ""),
            ArtistFixture.create("스텔라장", "Stella Jang", ""), ArtistFixture.create("민수", "", ""),
            ArtistFixture.create("다섯", "Dasutt", ""), ArtistFixture.create("델리스파이스", "Delispice", ""),
            ArtistFixture.create("소수빈", "", ""), ArtistFixture.create("가을방학", "", ""),
            ArtistFixture.create("신인류", "", ""), ArtistFixture.create("dosii", "도시", ""),
            ArtistFixture.create("보수동쿨러", "", ""), ArtistFixture.create("이고도", "", ""),
            ArtistFixture.create("결", "KYUL", ""), ArtistFixture.create("정우", "", ""),
            ArtistFixture.create("유라", "youra", ""), ArtistFixture.create("알레프", "ALEPH", "")
    );


    @BeforeEach
    void setup() {
        artistMongoCommandRepository.deleteAll();

        long startBulk = System.currentTimeMillis();

        // 단건 insert 주석 처리
    /*
    for (int i = 0; i < 20000; i++) {
        Artist sampleTemplate = templates.get(i % templates.size());

        Artist sample = ArtistFixture.create(
                Long.valueOf(i + 1),
                sampleTemplate.getName(),
                sampleTemplate.getSecondName(),
                sampleTemplate.getThirdName()
        );
        Artist saved = artistCommandRepository.save(sample);
        artistMongoCommandRepository.save(ArtistMongo.of(saved));
    }
    */

        // bulk insert
        List<Artist> artistBatch = new java.util.ArrayList<>();
        List<ArtistMongo> mongoBatch = new java.util.ArrayList<>();
        for (int i = 0; i < 20000; i++) {
            Artist sampleTemplate = templates.get(i % templates.size());
            Artist sample = ArtistFixture.create(
                    Long.valueOf(i + 1),
                    sampleTemplate.getName(),
                    sampleTemplate.getSecondName(),
                    sampleTemplate.getThirdName()
            );
            artistBatch.add(sample);
        }

        // MySQL bulk save
        List<Artist> savedArtists = artistCommandRepository.saveAll(artistBatch);

        // Mongo bulk save
        for (Artist saved : savedArtists) {
            mongoBatch.add(ArtistMongo.of(saved));
        }
        artistMongoCommandRepository.saveAll(mongoBatch);

        long endBulk = System.currentTimeMillis();
        System.out.println("Bulk insert " + 20000 + "개 걸린 시간: " + (endBulk - startBulk) + " ms");
    }

    @Test
    void searchPerformanceTest() {
        List<String> keywords = List.of(
                "검", "검정", "잔", "잔나", "최", "최유",
                "실", "실리", "sil", "Silica", "데", "데이", "Dam",
                "새", "10", "십", "혁", "HY",
                "카", "카더", "car", "너드", "Nerd",
                "허", "우", "OO", "백아", "Baek",
                "브", "Bro", "쏜", "THOR",
                "S", "SU", "설",
                "한", "HAN", "윤", "백예", "yer",
                "유다", "Yd", "신지", "웨이", "wave",
                "L", "LU", "루시",
                "TO", "TOU", "터치",
                "알레", "ALE",
                "하현", "김사",
                "언니", "나상", "Band",
                "박소", "짙", "밍", "Ming",
                "뜻돌", "라쿠", "Lac",
                "치즈", "CHE", "김현",
                "볼빨", "스텔", "Stel",
                "민", "다섯", "Das",
                "델리", "Del", "소수",
                "가을", "신인",
                "dos", "도시",
                "보수", "이고",
                "결", "KYU",
                "정우",
                "유라", "you",
                "알레", "ALEP"
        );

        // 각 케이스별 latency(ms) 저장
        List<Long> mysqlLikeServiceLatencies = new java.util.ArrayList<>();
        List<Long> mongoAtlasServiceLatencies = new java.util.ArrayList<>();
        List<Long> mysqlLikeRepoLatencies = new java.util.ArrayList<>();
        List<Long> mongoAtlasRepoLatencies = new java.util.ArrayList<>();

        // MySQL LIKE 테스트 - service
        for (String keyword : keywords) {
            long start = System.nanoTime();
            artistQueryService.searchArtistsWithLike(keyword, PageRequest.of(0, 5));
            mysqlLikeServiceLatencies.add((System.nanoTime() - start) / 1_000_000);
        }

        // Mongo Atlas Search 테스트 - service
        for (String keyword : keywords) {
            long start = System.nanoTime();
            artistQueryService.searchArtists(keyword, PageRequest.of(0, 5));
            mongoAtlasServiceLatencies.add((System.nanoTime() - start) / 1_000_000);
        }

        // MySQL LIKE 테스트 - repository
        for (String keyword : keywords) {
            long start = System.nanoTime();
            artistQueryRepository.findAllByQuery(keyword, PageRequest.of(0, 5));
            mysqlLikeRepoLatencies.add((System.nanoTime() - start) / 1_000_000);
        }

        // Mongo Atlas Search 테스트 - repository
        for (String keyword : keywords) {
            long start = System.nanoTime();
            artistMongoQueryRepository.searchArtistIdsByName(keyword, 0, 12);
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
        artistMongoCommandRepository.deleteAll();
    }
}

