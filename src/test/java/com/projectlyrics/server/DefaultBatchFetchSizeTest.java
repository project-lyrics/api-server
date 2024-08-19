package com.projectlyrics.server;

import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.artist.repository.ArtistCommandRepository;
import com.projectlyrics.server.domain.note.entity.Note;
import com.projectlyrics.server.domain.note.entity.NoteCreate;
import com.projectlyrics.server.domain.note.entity.NoteStatus;
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
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import java.util.List;

import static com.projectlyrics.server.domain.bookmark.domain.QBookmark.bookmark;
import static com.projectlyrics.server.domain.comment.domain.QComment.comment;
import static com.projectlyrics.server.domain.like.domain.QLike.like;

public class DefaultBatchFetchSizeTest extends IntegrationTest {

    @Autowired
    UserCommandRepository userCommandRepository;

    @Autowired
    ArtistCommandRepository artistCommandRepository;

    @Autowired
    SongCommandRepository songCommandRepository;

    @Autowired
    NoteCommandRepository noteCommandRepository;

    @Autowired
    NoteQueryRepository defaultBatchFetchSizeRepository;

    @Autowired
    JPAQueryFactory jpaQueryFactory;

    NoteQueryRepository noJoinRepository;

    private User user;
    private Artist artist;
    private Song song;

    @BeforeEach
    void setUp() {
        noJoinRepository = new NoJoinQueryDslNoteQueryRepository(jpaQueryFactory);

        user = userCommandRepository.save(UserFixture.create());
        artist = artistCommandRepository.save(ArtistFixture.create());
        song = songCommandRepository.save(SongFixture.create(artist));

        for (int i = 0; i < 100; i++) {
            noteCommandRepository.save(Note.create(new NoteCreate(
                    "content",
                    null,
                    null,
                    NoteStatus.PUBLISHED,
                    user,
                    song
            )));
        }
    }

    @Test
    void default_batch_fetch_size를_활용하여_노트를_조회한다() {
        // given
        long start = System.currentTimeMillis();

        // when
        List<Note> notes = defaultBatchFetchSizeRepository.findAllByUserId(user.getId(), null, PageRequest.ofSize(100)).getContent();

        notes.forEach(note -> {
            note.getComments();
            note.getLikes();
            note.getBookmarks();
        });

        // then
        System.out.println("elapsed time : " + (System.currentTimeMillis() - start) + "ms");
    }

    @Test
    void join하지_않고_노트를_조회한다() {
        // given
        long start = System.currentTimeMillis();

        // when
        List<Note> notes = noJoinRepository.findAllByUserId(user.getId(), null, PageRequest.ofSize(100)).getContent();

        notes.forEach(note -> {
            findAllCommentsBy(note);
            findAllLikesBy(note);
            findAllBookmarksBy(note);
        });

        // then
        System.out.println("elapsed time : " + (System.currentTimeMillis() - start) + "ms");
    }

    private void findAllBookmarksBy(Note note) {
        jpaQueryFactory
                .selectFrom(bookmark)
                .where(
                        bookmark.note.id.eq(note.getId()),
                        bookmark.deletedAt.isNull()
                )
                .limit(10)
                .fetch();
    }

    private void findAllLikesBy(Note note) {
        jpaQueryFactory
                .selectFrom(like)
                .leftJoin(like.user).fetchJoin()
                .where(
                        like.note.id.eq(note.getId()),
                        like.deletedAt.isNull()
                )
                .limit(10)
                .fetch();
    }

    private void findAllCommentsBy(Note note) {
        jpaQueryFactory
                .selectFrom(comment)
                .leftJoin(comment.writer).fetchJoin()
                .leftJoin(comment.note).fetchJoin()
                .where(
                        comment.note.id.eq(note.getId()),
                        comment.deletedAt.isNull()
                )
                .limit(10)
                .fetch();
    }
}
