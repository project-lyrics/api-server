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

@SpringBootTest
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
                "검", "10", "하현", "보수동", "언니네 이발관",
                "s", "St", "Stella Ja", "Mingginyu"
        );

        int runsPerKeyword = 10; // 키워드당 반복 횟수

        long mongoAtlasRepositoryTotal = 0;
        long mysqlLikeRepositoryTotal = 0;
        long mongoAtlasServiceTotal = 0;
        long mysqlLikeServiceTotal = 0;

        // DB 캐시 / JIT 최적화 피하고 정확한 성능 측정을 위해 반복문 분리
        for (String keyword : keywords) {
            for (int i = 0; i < runsPerKeyword; i++) {
                // Mongo Atlas 검색 (service)
                long start = System.nanoTime();
                artistQueryService.searchArtists(keyword, PageRequest.of(0, 12));
                mongoAtlasServiceTotal += System.nanoTime() - start;

                // MySQL LIKE 검색 (service)
                start = System.nanoTime();
                artistQueryService.searchArtistsWithLike(keyword, PageRequest.of(0, 12));
                mysqlLikeServiceTotal += System.nanoTime() - start;
            }
        }

        for (String keyword : keywords) {
            for (int i = 0; i < runsPerKeyword; i++) {
                // Mongo Atlas 검색 (repository)
                long start = System.nanoTime();
                artistMongoQueryRepository.searchArtistIdsByName(keyword, 0, 12);
                mongoAtlasRepositoryTotal += System.nanoTime() - start;

                // MySQL LIKE 검색 (repository)
                start = System.nanoTime();
                artistQueryRepository.findAllByQuery(keyword, PageRequest.of(0, 12));
                mysqlLikeRepositoryTotal += System.nanoTime() - start;
            }
        }

        int totalRuns = keywords.size() * runsPerKeyword;

        System.out.println(
                "Mongo Atlas Repository 평균(ms): " + mongoAtlasRepositoryTotal / (double) totalRuns / 1_000_000.0);
        System.out.println(
                "MySQL LIKE Repository 평균(ms): " + mysqlLikeRepositoryTotal / (double) totalRuns / 1_000_000.0);
        System.out.println("Mongo Atlas Service 평균(ms): " + mongoAtlasServiceTotal / (double) totalRuns / 1_000_000.0);
        System.out.println("MySQL LIKE Service 평균(ms): " + mysqlLikeServiceTotal / (double) totalRuns / 1_000_000.0);
    }

    @AfterEach
    void cleanup() {
        artistMongoCommandRepository.deleteAll();
    }
}

