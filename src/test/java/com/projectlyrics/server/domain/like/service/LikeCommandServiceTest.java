package com.projectlyrics.server.domain.like.service;

import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.artist.repository.ArtistCommandRepository;
import com.projectlyrics.server.domain.bookmark.exception.BookmarkAlreadyExistsException;
import com.projectlyrics.server.domain.like.domain.Like;
import com.projectlyrics.server.domain.like.exception.LikeAlreadyExistsException;
import com.projectlyrics.server.domain.like.exception.LikeNotFoundException;
import com.projectlyrics.server.domain.like.repository.LikeQueryRepository;
import com.projectlyrics.server.domain.note.dto.request.NoteCreateRequest;
import com.projectlyrics.server.domain.note.entity.Note;
import com.projectlyrics.server.domain.note.entity.NoteBackground;
import com.projectlyrics.server.domain.note.entity.NoteCreate;
import com.projectlyrics.server.domain.note.entity.NoteStatus;
import com.projectlyrics.server.domain.note.entity.NoteType;
import com.projectlyrics.server.domain.note.repository.NoteCommandRepository;
import com.projectlyrics.server.domain.note.repository.NoteQueryRepository;
import com.projectlyrics.server.domain.song.entity.Song;
import com.projectlyrics.server.domain.song.repository.SongCommandRepository;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.repository.UserCommandRepository;
import com.projectlyrics.server.support.IntegrationTest;
import com.projectlyrics.server.support.fixture.ArtistFixture;
import com.projectlyrics.server.support.fixture.SongFixture;
import com.projectlyrics.server.support.fixture.UserFixture;
import com.querydsl.core.NonUniqueResultException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

class LikeCommandServiceTest extends IntegrationTest {

    @Autowired
    UserCommandRepository userCommandRepository;

    @Autowired
    ArtistCommandRepository artistCommandRepository;

    @Autowired
    SongCommandRepository songCommandRepository;

    @Autowired
    NoteQueryRepository noteQueryRepository;

    @Autowired
    NoteCommandRepository noteCommandRepository;

    @Autowired
    LikeQueryRepository likeQueryRepository;

    @Autowired
    LikeCommandService sut;

    private User user;
    private Artist artist;
    private Song song;
    private Note note;

    @BeforeEach
    void setUp() {
        user = userCommandRepository.save(UserFixture.create());
        artist = artistCommandRepository.save(ArtistFixture.create());
        song = songCommandRepository.save(SongFixture.create(artist));
        NoteCreateRequest noteCreateRequest = new NoteCreateRequest(
                "content",
                "lyrics",
                NoteBackground.DEFAULT,
                NoteStatus.PUBLISHED,
                NoteType.FREE,
                song.getId()
        );
        note = noteCommandRepository.save(Note.create(NoteCreate.from(noteCreateRequest, user, song)));
    }

    @Test
    void 좋아요를_저장해야_한다() {
        // when
        Like like = sut.create(note.getId(), user.getId());

        // then
        assertAll(
                () -> assertThat(like.getUser()).isEqualTo(user),
                () -> assertThat(like.getNote()).isEqualTo(note)
        );
    }

    @Test
    void 좋아요가_이미_있을_경우_저장하지_않고_예외를_발생시켜야_한다() {
        // given
        sut.create(note.getId(), user.getId());

        // when, then
        assertThatThrownBy(() -> sut.create(note.getId(), user.getId()))
                .isInstanceOf(LikeAlreadyExistsException.class);
    }

//    @Test
//    void 좋아요가_동시다발적으로_저장될_떄도_중복_좋아요가_생기지_않아야_한다() {
//        //given
//        int threadCount = 100;
//        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
//        CountDownLatch readyLatch = new CountDownLatch(threadCount);
//        CountDownLatch startLatch = new CountDownLatch(1);
//        CountDownLatch doneLatch = new CountDownLatch(threadCount);
//        AtomicInteger duplicateErrorCount = new AtomicInteger(0);
//
//        //when
//        for (int i = 0; i < threadCount; i++) {
//            executorService.execute(() -> {
//                readyLatch.countDown(); // 스레드가 준비 상태임을 알림
//                try {
//                    startLatch.await(); // 모든 스레드가 준비될 때까지 대기
//                    sut.create(note.getId(), user.getId());
//                } catch (LikeAlreadyExistsException | NonUniqueResultException e) {
//                    duplicateErrorCount.getAndIncrement();
//                } catch (InterruptedException e) {
//                    Thread.currentThread().interrupt();
//                } finally {
//                    doneLatch.countDown();
//                }
//            });
//        }
//
//        try {
//            readyLatch.await(); // 모든 스레드가 준비될 때까지 대기
//            startLatch.countDown(); // 모든 스레드가 동시에 실행 시작
//            doneLatch.await(); // 모든 스레드의 실행이 끝날 때까지 대기
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//            throw new RuntimeException(e);
//        }
//
//        //then
//        assertAll(
//                () -> assertThat(likeQueryRepository.findByNoteIdAndUserId(note.getId(), user.getId())).isPresent(),
//                () -> assertThat(duplicateErrorCount.get()).isEqualTo(threadCount - 1)
//        );
//    }

    @Test
    void 좋아요를_삭제해야_한다() {
        // given
        sut.create(note.getId(), user.getId());

        // when
        sut.delete(note.getId(), user.getId());

        // then
        assertThat(likeQueryRepository.findByNoteIdAndUserId(note.getId(), user.getId())).isEmpty();
    }

    @Test
    void 존재하지_않는_좋아요를_삭제하지_않고_예외를_발생시켜야_한다() {
        // when, then
        assertThatThrownBy(() -> sut.delete(99L, 99L))
                .isInstanceOf(LikeNotFoundException.class);
    }
}