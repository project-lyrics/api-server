package com.projectlyrics.server.domain.note.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.artist.repository.ArtistCommandRepository;
import com.projectlyrics.server.domain.block.repository.BlockCommandRepository;
import com.projectlyrics.server.domain.bookmark.repository.BookmarkCommandRepository;
import com.projectlyrics.server.domain.comment.domain.Comment;
import com.projectlyrics.server.domain.comment.repository.CommentCommandRepository;
import com.projectlyrics.server.domain.common.dto.util.CursorBasePaginatedResponse;
import com.projectlyrics.server.domain.favoriteartist.repository.FavoriteArtistCommandRepository;
import com.projectlyrics.server.domain.note.dto.request.NoteCreateRequest;
import com.projectlyrics.server.domain.note.dto.response.NoteDetailResponse;
import com.projectlyrics.server.domain.note.dto.response.NoteGetResponse;
import com.projectlyrics.server.domain.note.entity.Note;
import com.projectlyrics.server.domain.note.entity.NoteBackground;
import com.projectlyrics.server.domain.note.entity.NoteStatus;
import com.projectlyrics.server.domain.note.entity.NoteType;
import com.projectlyrics.server.domain.song.entity.Song;
import com.projectlyrics.server.domain.song.repository.SongCommandRepository;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.repository.UserCommandRepository;
import com.projectlyrics.server.support.IntegrationTest;
import com.projectlyrics.server.support.fixture.ArtistFixture;
import com.projectlyrics.server.support.fixture.BlockFixture;
import com.projectlyrics.server.support.fixture.BookmarkFixture;
import com.projectlyrics.server.support.fixture.CommentFixture;
import com.projectlyrics.server.support.fixture.FavoriteArtistFixture;
import com.projectlyrics.server.support.fixture.SongFixture;
import com.projectlyrics.server.support.fixture.UserFixture;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class NoteQueryServiceTest extends IntegrationTest {

    @Autowired
    UserCommandRepository userCommandRepository;

    @Autowired
    ArtistCommandRepository artistCommandRepository;

    @Autowired
    FavoriteArtistCommandRepository favoriteArtistCommandRepository;

    @Autowired
    SongCommandRepository songCommandRepository;

    @Autowired
    NoteCommandService noteCommandService;

    @Autowired
    CommentCommandRepository commentCommandRepository;

    @Autowired
    BookmarkCommandRepository bookmarkCommandRepository;

    @Autowired
    BlockCommandRepository blockCommandRepository;

    @Autowired
    NoteQueryService sut;

    private User user, user1, user2;
    private Artist unlikedArtist;
    private Artist likedArtist;
    private Song unlikedArtistSong;
    private Song likedArtistSong;
    private NoteCreateRequest unlikedArtistSongNoteRequest;
    private NoteCreateRequest likedArtistSongNoteRequest;
    private NoteCreateRequest likedArtistSongNoteRequestWithoutLyrics;

    @BeforeEach
    void setUp() {
        user = userCommandRepository.save(UserFixture.create());
        user1 = userCommandRepository.save(UserFixture.create());
        user2 = userCommandRepository.save(UserFixture.create());
        unlikedArtist = artistCommandRepository.save(ArtistFixture.create());
        likedArtist = artistCommandRepository.save(ArtistFixture.create());
        unlikedArtistSong = songCommandRepository.save(SongFixture.create(unlikedArtist));
        likedArtistSong = songCommandRepository.save(SongFixture.create(likedArtist));
        unlikedArtistSongNoteRequest = new NoteCreateRequest(
                "content",
                "lyrics",
                NoteBackground.DEFAULT,
                NoteStatus.PUBLISHED,
                NoteType.FREE,
                unlikedArtistSong.getId()
        );
        likedArtistSongNoteRequest = new NoteCreateRequest(
                "content",
                "lyrics",
                NoteBackground.DEFAULT,
                NoteStatus.PUBLISHED,
                NoteType.FREE,
                likedArtistSong.getId()
        );
        likedArtistSongNoteRequestWithoutLyrics = new NoteCreateRequest(
                "content",
                null,
                null,
                NoteStatus.PUBLISHED,
                NoteType.FREE,
                likedArtistSong.getId()
        );
    }

    @Test
    void 노트_id와_일치하는_노트를_조회해야_한다() {
        // given
        Note note = noteCommandService.create(likedArtistSongNoteRequest, user.getId());
        Comment comment1 = commentCommandRepository.save(CommentFixture.create(note, user));
        Comment comment2 = commentCommandRepository.save(CommentFixture.create(note, user));

        // when
        NoteDetailResponse result = sut.getNoteById(note.getId(), UserFixture.create().getId());

        // then
        assertAll(
                () -> assertThat(result.id()).isEqualTo(note.getId()),
                () -> assertThat(result.content()).isEqualTo(note.getContent()),
                () -> assertThat(result.lyrics().lyrics()).isEqualTo(note.getLyrics().getContent()),
                () -> assertThat(result.lyrics().background()).isEqualTo(note.getLyrics().getBackground().getType()),
                () -> assertThat(result.status()).isEqualTo(note.getNoteStatus().name()),
                () -> assertThat(result.song().id()).isEqualTo(note.getSong().getId()),
                () -> assertThat(result.publisher().id()).isEqualTo(note.getPublisher().getId()),
                () -> assertThat(result.comments().size()).isEqualTo(2),
                () -> assertThat(result.comments().get(0).id()).isEqualTo(comment1.getId()),
                () -> assertThat(result.comments().get(1).id()).isEqualTo(comment2.getId())
        );
    }

    @Test
    void 노트_id와_일치하는_노트_조회시_차단된_사용자의_댓글은_제외하고_조회해야_한다() {
        // given
        Note note = noteCommandService.create(likedArtistSongNoteRequest, user.getId());
        commentCommandRepository.save(CommentFixture.create(note, user1));
        Comment comment2 = commentCommandRepository.save(CommentFixture.create(note, user2));
        blockCommandRepository.save(BlockFixture.create(user, user1));

        // when
        NoteDetailResponse result = sut.getNoteById(note.getId(), user.getId());

        // then
        assertAll(
                () -> assertThat(result.comments().size()).isEqualTo(1),
                () -> assertThat(result.comments().get(0).id()).isEqualTo(comment2.getId())
        );
    }

    @Test
    void 노트_id와_일치하는_노트_조회시_모든_노트의_댓글의_작성자를_차단해도_노트정보를_조회해야_한다() {
        // given
        Note note = noteCommandService.create(likedArtistSongNoteRequest, user.getId());
        commentCommandRepository.save(CommentFixture.create(note, user1));
        commentCommandRepository.save(CommentFixture.create(note, user2));
        blockCommandRepository.save(BlockFixture.create(user, user1));
        blockCommandRepository.save(BlockFixture.create(user, user2));

        // when
        NoteDetailResponse result = sut.getNoteById(note.getId(), user.getId());

        // then
        assertAll(
                () -> assertThat(result.id()).isNotNull(),
                () -> assertThat(result.comments().size()).isEqualTo(0)
        );
    }

    @Test
    void 사용자_id와_일치하는_작성자와_연관된_노트_리스트를_최신순으로_조회해야_한다() {
        // given
        List<Note> notes = new ArrayList<>();

        IntStream.range(0, 10)
                .forEach(i -> {
                    Note note = noteCommandService.create(likedArtistSongNoteRequest, user.getId());
                    notes.add(note);
                });

        // when
        CursorBasePaginatedResponse<NoteGetResponse> result = sut.getNotesByUserId(true, null, user.getId(), null, 10);

        // then
        assertAll(
                () -> assertThat(result.data().size()).isEqualTo(10),
                () -> assertThat(result.data().get(0).id()).isEqualTo(notes.get(9).getId()),
                () -> assertThat(result.data().get(1).id()).isEqualTo(notes.get(8).getId()),
                () -> assertThat(result.data().get(2).id()).isEqualTo(notes.get(7).getId())
        );
    }

    @Test
    void 사용자_id와_일치하는_작성자와_연관된_가사가_없는_노트_리스트도_포함하여_최신순으로_조회해야_한다() {
        // given
        List<Note> notes = new ArrayList<>();

        IntStream.range(0, 10)
                .forEach(i -> {
                    Note note = noteCommandService.create(likedArtistSongNoteRequestWithoutLyrics, user.getId());
                    notes.add(note);
                });

        // when
        CursorBasePaginatedResponse<NoteGetResponse> result = sut.getNotesByUserId(false, null, user.getId(), null, 10);

        // then
        assertAll(
                () -> assertThat(result.data().size()).isEqualTo(10),
                () -> assertThat(result.data().get(0).id()).isEqualTo(notes.get(9).getId()),
                () -> assertThat(result.data().get(1).id()).isEqualTo(notes.get(8).getId()),
                () -> assertThat(result.data().get(2).id()).isEqualTo(notes.get(7).getId())
        );
    }

    @Test
    void 사용자가_좋아하는_아티스트와_관련된_노트를_최신순으로_조회해야_한다() {
        // given
        favoriteArtistCommandRepository.save(FavoriteArtistFixture.create(user, likedArtist));

        noteCommandService.create(unlikedArtistSongNoteRequest, user.getId());
        noteCommandService.create(unlikedArtistSongNoteRequest, user.getId());
        Note likedArtistSongNote1 = noteCommandService.create(likedArtistSongNoteRequest, user.getId());
        Note likedArtistSongNote2 = noteCommandService.create(likedArtistSongNoteRequest, user.getId());
        Note likedArtistSongNote3 = noteCommandService.create(likedArtistSongNoteRequest, user.getId());

        // when
        CursorBasePaginatedResponse<NoteGetResponse> result = sut.getNotes(true, true, user.getId(), null, 5);

        // then
        assertAll(
                () -> assertThat(result.data().size()).isEqualTo(3),
                () -> assertThat(result.data().get(0).id()).isEqualTo(likedArtistSongNote3.getId()),
                () -> assertThat(result.data().get(1).id()).isEqualTo(likedArtistSongNote2.getId()),
                () -> assertThat(result.data().get(2).id()).isEqualTo(likedArtistSongNote1.getId())
        );
    }

    @Test
    void 사용자가_좋아하는_아티스트와_관련된_노트_조회시_차단된_사용자의_노트는_제외하고_조회해야_한다() {
        // given
        favoriteArtistCommandRepository.save(FavoriteArtistFixture.create(user, likedArtist));

        noteCommandService.create(unlikedArtistSongNoteRequest, user.getId());
        noteCommandService.create(unlikedArtistSongNoteRequest, user.getId());
        noteCommandService.create(likedArtistSongNoteRequest, user1.getId());
        noteCommandService.create(likedArtistSongNoteRequest, user1.getId());
        Note likedArtistSongNote1 = noteCommandService.create(likedArtistSongNoteRequest, user2.getId());
        Note likedArtistSongNote2 = noteCommandService.create(likedArtistSongNoteRequest, user2.getId());
        blockCommandRepository.save(BlockFixture.create(user, user1));

        // when
        CursorBasePaginatedResponse<NoteGetResponse> result = sut.getNotes(true, true, user.getId(), null, 5);

        // then
        assertAll(
                () -> assertThat(result.data().size()).isEqualTo(2),
                () -> assertThat(result.data().get(0).id()).isEqualTo(likedArtistSongNote2.getId()),
                () -> assertThat(result.data().get(1).id()).isEqualTo(likedArtistSongNote1.getId())
        );
    }

    @Test
    void 특정_아티스트와_관련된_노트를_최신순으로_조회해야_한다() {
        // given
        Note note1 = noteCommandService.create(likedArtistSongNoteRequest, user.getId());
        Note note2 = noteCommandService.create(likedArtistSongNoteRequest, user.getId());
        Note note3 = noteCommandService.create(likedArtistSongNoteRequest, user.getId());

        // when
        CursorBasePaginatedResponse<NoteGetResponse> result1 = sut.getNotesByArtistId(false, likedArtist.getId(), user.getId(), null, 5);
        CursorBasePaginatedResponse<NoteGetResponse> result2 = sut.getNotesByArtistId(false, unlikedArtistSong.getId(), user.getId(), null, 5);

        // then
        assertAll(
                () -> assertThat(result1.data().size()).isEqualTo(3),
                () -> assertThat(result1.data().get(0).id()).isEqualTo(note3.getId()),
                () -> assertThat(result1.data().get(1).id()).isEqualTo(note2.getId()),
                () -> assertThat(result1.data().get(2).id()).isEqualTo(note1.getId()),
                () -> assertThat(result2.data().size()).isEqualTo(0)
        );
    }

    @Test
    void 특정_아티스트와_관련된_노트_중_가사가_있는_것만_최신순으로_조회해야_한다() {
        // given
        NoteCreateRequest noLyricsRequest = new NoteCreateRequest(
                "content",
                null,
                null,
                NoteStatus.PUBLISHED,
                NoteType.FREE,
                likedArtistSong.getId()
        );

        Note note1 = noteCommandService.create(likedArtistSongNoteRequest, user.getId());
        Note note2 = noteCommandService.create(noLyricsRequest, user.getId());
        Note note3 = noteCommandService.create(likedArtistSongNoteRequest, user.getId());

        // when
        CursorBasePaginatedResponse<NoteGetResponse> result = sut.getNotesByArtistId(true, likedArtist.getId(), user.getId(), null, 5);

        // then
        assertAll(
                () -> assertThat(result.data().size()).isEqualTo(2),
                () -> assertThat(result.data().get(0).id()).isEqualTo(note3.getId()),
                () -> assertThat(result.data().get(1).id()).isEqualTo(note1.getId())
        );
    }

    @Test
    void 특정_아티스트와_관련된_노트_조회시_차단된_사용자가_작성한_노트는_제외하고_조회해야_한다() {
        // given
        noteCommandService.create(likedArtistSongNoteRequest, user1.getId());
        noteCommandService.create(likedArtistSongNoteRequest, user1.getId());
        Note note1 = noteCommandService.create(likedArtistSongNoteRequest, user2.getId());
        Note note2 = noteCommandService.create(likedArtistSongNoteRequest, user2.getId());
        blockCommandRepository.save(BlockFixture.create(user, user1));

        // when
        CursorBasePaginatedResponse<NoteGetResponse> result = sut.getNotesByArtistId(false, likedArtist.getId(), user.getId(), null, 5);

        // then
        assertAll(
                () -> assertThat(result.data().size()).isEqualTo(2),
                () -> assertThat(result.data().get(0).id()).isEqualTo(note2.getId()),
                () -> assertThat(result.data().get(1).id()).isEqualTo(note1.getId())
        );
    }

    @Test
    void 특정_곡과_관련된_노트를_최신순으로_조회해야_한다() {
        // given
        Note note1 = noteCommandService.create(likedArtistSongNoteRequest, user.getId());
        Note note2 = noteCommandService.create(likedArtistSongNoteRequest, user.getId());
        Note note3 = noteCommandService.create(likedArtistSongNoteRequest, user.getId());

        // when
        CursorBasePaginatedResponse<NoteGetResponse> result1 = sut.getNotesBySongId(false, likedArtistSong.getId(), user.getId(), null, 5);
        CursorBasePaginatedResponse<NoteGetResponse> result2 = sut.getNotesBySongId(false, unlikedArtistSong.getId(), user.getId(), null, 5);

        // then
        assertAll(
                () -> assertThat(result1.data().size()).isEqualTo(3),
                () -> assertThat(result1.data().get(0).id()).isEqualTo(note3.getId()),
                () -> assertThat(result1.data().get(1).id()).isEqualTo(note2.getId()),
                () -> assertThat(result1.data().get(2).id()).isEqualTo(note1.getId()),
                () -> assertThat(result2.data().size()).isEqualTo(0)
        );
    }

    @Test
    void 특정_곡과_관련된_노트_조회시_차단된_사용자가_작성한_노트는_제외하고_조회해야_한다() {
        // given
        noteCommandService.create(likedArtistSongNoteRequest, user1.getId());
        noteCommandService.create(likedArtistSongNoteRequest, user1.getId());
        Note note1 = noteCommandService.create(likedArtistSongNoteRequest, user2.getId());
        Note note2 = noteCommandService.create(likedArtistSongNoteRequest, user2.getId());
        blockCommandRepository.save(BlockFixture.create(user, user1));

        // when
        CursorBasePaginatedResponse<NoteGetResponse> result = sut.getNotesBySongId(false, likedArtistSong.getId(), user.getId(), null, 5);

        // then
        assertAll(
                () -> assertThat(result.data().size()).isEqualTo(2),
                () -> assertThat(result.data().get(0).id()).isEqualTo(note2.getId()),
                () -> assertThat(result.data().get(1).id()).isEqualTo(note1.getId())
        );
    }

    @Test
    void 사용자가_북마크한_노트를_최신순으로_조회해야_한다() {
        // given
        Note bookmarkedNote1 = noteCommandService.create(likedArtistSongNoteRequest, user.getId());
        Note bookmarkedNote2 = noteCommandService.create(likedArtistSongNoteRequest, user.getId());
        noteCommandService.create(likedArtistSongNoteRequest, user.getId());

        bookmarkCommandRepository.save(BookmarkFixture.create(user, bookmarkedNote1));
        bookmarkCommandRepository.save(BookmarkFixture.create(user, bookmarkedNote2));

        // when
        CursorBasePaginatedResponse<NoteGetResponse> result = sut.getBookmarkedNotes(true, likedArtist.getId(), user.getId(), null, 5);

        // then
        assertAll(
                () -> assertThat(result.data().size()).isEqualTo(2),
                () -> assertThat(result.data().get(0).id()).isEqualTo(bookmarkedNote2.getId()),
                () -> assertThat(result.data().get(1).id()).isEqualTo(bookmarkedNote1.getId())
        );
    }

    @Test
    void 사용자가_북마크한_노트_중에서_특정_아티스트의_노트만_최신순으로_조회해야_한다() {
        // given
        Note bookmarkedNoteOfLikedArtist = noteCommandService.create(likedArtistSongNoteRequest, user.getId());
        Note bookmarkedNoteOfUnlikedArtist = noteCommandService.create(unlikedArtistSongNoteRequest, user.getId());

        bookmarkCommandRepository.save(BookmarkFixture.create(user, bookmarkedNoteOfLikedArtist));
        bookmarkCommandRepository.save(BookmarkFixture.create(user, bookmarkedNoteOfUnlikedArtist));

        // when
        CursorBasePaginatedResponse<NoteGetResponse> result = sut.getBookmarkedNotes(true, likedArtist.getId(), user.getId(), null, 5);

        // then
        assertAll(
                () -> assertThat(result.data().size()).isEqualTo(1),
                () -> assertThat(result.data().getFirst().id()).isEqualTo(bookmarkedNoteOfLikedArtist.getId())
        );
    }

    @Test
    void 사용자가_북마크한_노트_중에서_특정_아티스트와_관련된_노트를_최신순으로_조회해야_한다() {
        // given
        Note likedArtistNote = noteCommandService.create(likedArtistSongNoteRequest, user.getId());
        Note unlikedArtistNote = noteCommandService.create(unlikedArtistSongNoteRequest, user.getId());

        bookmarkCommandRepository.save(BookmarkFixture.create(user, likedArtistNote));
        bookmarkCommandRepository.save(BookmarkFixture.create(user, unlikedArtistNote));

        // when
        CursorBasePaginatedResponse<NoteGetResponse> result = sut.getBookmarkedNotes(true, likedArtist.getId(), user.getId(), null, 5);

        // then
        assertAll(
                () -> assertThat(result.data().size()).isEqualTo(1),
                () -> assertThat(result.data().getFirst().id()).isEqualTo(likedArtistNote.getId())
        );
    }

    @Test
    void 사용자가_북마크한_노트_조회시_차단된_사용자가_작성한_노트는_제외하고_조회해야_한다() {
        // given
        Note bookmarkedNote1 = noteCommandService.create(likedArtistSongNoteRequest, user1.getId());
        Note bookmarkedNote2 = noteCommandService.create(likedArtistSongNoteRequest, user1.getId());
        Note bookmarkedNote3 = noteCommandService.create(likedArtistSongNoteRequest, user2.getId());
        Note bookmarkedNote4 = noteCommandService.create(likedArtistSongNoteRequest, user2.getId());
        noteCommandService.create(likedArtistSongNoteRequest, user.getId());

        bookmarkCommandRepository.save(BookmarkFixture.create(user, bookmarkedNote1));
        bookmarkCommandRepository.save(BookmarkFixture.create(user, bookmarkedNote2));
        bookmarkCommandRepository.save(BookmarkFixture.create(user, bookmarkedNote3));
        bookmarkCommandRepository.save(BookmarkFixture.create(user, bookmarkedNote4));

        blockCommandRepository.save(BlockFixture.create(user, user1));

        // when
        CursorBasePaginatedResponse<NoteGetResponse> result = sut.getBookmarkedNotes(true, likedArtist.getId(), user.getId(), null, 5);

        // then
        assertAll(
                () -> assertThat(result.data().size()).isEqualTo(2),
                () -> assertThat(result.data().get(0).id()).isEqualTo(bookmarkedNote4.getId()),
                () -> assertThat(result.data().get(1).id()).isEqualTo(bookmarkedNote3.getId())
        );
    }
}