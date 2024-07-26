package com.projectlyrics.server.domain.note.service;

import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.artist.repository.ArtistCommandRepository;
import com.projectlyrics.server.domain.note.dto.request.NoteCreateRequest;
import com.projectlyrics.server.domain.note.dto.request.NoteUpdateRequest;
import com.projectlyrics.server.domain.note.entity.Note;
import com.projectlyrics.server.domain.note.entity.NoteBackground;
import com.projectlyrics.server.domain.note.entity.NoteStatus;
import com.projectlyrics.server.domain.note.exception.InvalidNoteDeletionException;
import com.projectlyrics.server.domain.note.exception.InvalidNoteUpdateException;
import com.projectlyrics.server.domain.note.exception.TooManyDraftsException;
import com.projectlyrics.server.domain.note.repository.NoteQueryRepository;
import com.projectlyrics.server.domain.song.entity.Song;
import com.projectlyrics.server.domain.song.repository.SongCommandRepository;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.repository.UserCommandRepository;
import com.projectlyrics.server.support.IntegrationTest;
import com.projectlyrics.server.support.fixture.ArtistFixture;
import com.projectlyrics.server.support.fixture.SongFixture;
import com.projectlyrics.server.support.fixture.UserFixture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class NoteCommandServiceTest extends IntegrationTest {

    @Autowired
    UserCommandRepository userCommandRepository;

    @Autowired
    ArtistCommandRepository artistCommandRepository;

    @Autowired
    SongCommandRepository songCommandRepository;

    @Autowired
    NoteQueryRepository noteQueryRepository;

    @Autowired
    NoteCommandService sut;

    @Test
    void 노트를_저장해야_한다() throws Exception {
        // given
        User user = userCommandRepository.save(UserFixture.create());
        Artist artist = artistCommandRepository.save(ArtistFixture.create());
        Song song = songCommandRepository.save(SongFixture.create(artist));
        NoteCreateRequest request = new NoteCreateRequest(
                "content",
                "lyrics",
                NoteBackground.WHITE,
                NoteStatus.PUBLISHED,
                song.getId()
        );

        // when
        Note note = sut.create(request, user.getId());

        // then
        Slice<Note> result = noteQueryRepository.findAllByUserId(user.getId(), null, PageRequest.ofSize(5));
        assertAll(
                () -> assertThat(result.getContent().size()).isEqualTo(1),
                () -> assertThat(result.getContent().getFirst().getId()).isEqualTo(note.getId()),
                () -> assertThat(result.getContent().getFirst().getContent()).isEqualTo(note.getContent()),
                () -> assertThat(result.getContent().getFirst().getLyrics().getContent()).isEqualTo(note.getLyrics().getContent()),
                () -> assertThat(result.getContent().getFirst().getLyrics().getBackground()).isEqualTo(note.getLyrics().getBackground()),
                () -> assertThat(result.getContent().getFirst().getNoteStatus()).isEqualTo(note.getNoteStatus()),
                () -> assertThat(result.getContent().getFirst().getPublisher().getId()).isEqualTo(note.getPublisher().getId()),
                () -> assertThat(result.getContent().getFirst().getSong().getId()).isEqualTo(note.getSong().getId())
        );
    }

    @Test
    void 노트_저장시에_해당_노트의_상태가_드래프트인_경우_드래프트_개수를_체크해야_한다() throws Exception {
        // given
        User user = userCommandRepository.save(UserFixture.create());
        Artist artist = artistCommandRepository.save(ArtistFixture.create());
        Song song = songCommandRepository.save(SongFixture.create(artist));
        NoteCreateRequest request = new NoteCreateRequest(
                "content",
                "lyrics",
                NoteBackground.WHITE,
                NoteStatus.DRAFT,
                song.getId()
        );

        IntStream.range(0, 20)
                .forEach(i -> sut.create(request, user.getId()));

        // when, then
        assertThatThrownBy(() -> sut.create(request, user.getId()))
                .isInstanceOf(TooManyDraftsException.class);
    }

    @Test
    void 노트를_삭제해야_한다() throws Exception {
        // given
        User user = userCommandRepository.save(UserFixture.create());

        Artist artist = artistCommandRepository.save(ArtistFixture.create());
        Song song = songCommandRepository.save(SongFixture.create(artist));

        NoteCreateRequest request = new NoteCreateRequest(
                "content",
                "lyrics",
                NoteBackground.WHITE,
                NoteStatus.PUBLISHED,
                song.getId()
        );
        Note note = sut.create(request, user.getId());

        // when
        Slice<Note> beforeResult = noteQueryRepository.findAllByUserId(user.getId(), null, PageRequest.ofSize(5));
        sut.delete(user.getId(), note.getId());
        Slice<Note> afterResult = noteQueryRepository.findAllByUserId(user.getId(), null, PageRequest.ofSize(5));

        // then
        assertAll(
                () -> assertThat(beforeResult.getContent().size()).isEqualTo(1),
                () -> assertThat(afterResult.getContent().size()).isEqualTo(0)
        );
    }

    @Test
    void 사용자가_노트의_작성자가_아닌_경우_삭제시_예외를_발생시켜야_한다() throws Exception {
        // given
        User publisher = userCommandRepository.save(UserFixture.create());
        User unknownUser = userCommandRepository.save(UserFixture.create());

        Artist artist = artistCommandRepository.save(ArtistFixture.create());
        Song song = songCommandRepository.save(SongFixture.create(artist));

        NoteCreateRequest request = new NoteCreateRequest(
                "content",
                "lyrics",
                NoteBackground.WHITE,
                NoteStatus.PUBLISHED,
                song.getId()
        );
        Note note = sut.create(request, publisher.getId());

        // when, then
        assertThatThrownBy(() -> sut.delete(unknownUser.getId(), note.getId()))
                .isInstanceOf(InvalidNoteDeletionException.class);
    }

    @Test
    void 노트를_수정해야_한다() throws Exception {
        // given
        User user = userCommandRepository.save(UserFixture.create());

        Artist artist = artistCommandRepository.save(ArtistFixture.create());
        Song song = songCommandRepository.save(SongFixture.create(artist));

        NoteCreateRequest createRequest = new NoteCreateRequest(
                "content",
                "lyrics",
                NoteBackground.WHITE,
                NoteStatus.DRAFT,
                song.getId()
        );
        Note note = sut.create(createRequest, user.getId());

        NoteUpdateRequest updateRequest = new NoteUpdateRequest(
                "updated content",
                "updated lyrics",
                NoteBackground.WHITE,
                NoteStatus.PUBLISHED
        );

        // when
        Note updatedNote = sut.update(updateRequest, note.getId(), user.getId());

        // then
        assertAll(
                () -> assertThat(updatedNote.getId()).isEqualTo(note.getId()),
                () -> assertThat(updatedNote.getContent()).isEqualTo(updateRequest.content()),
                () -> assertThat(updatedNote.getLyrics().getContent()).isEqualTo(updateRequest.lyrics()),
                () -> assertThat(updatedNote.getLyrics().getBackground()).isEqualTo(updateRequest.background()),
                () -> assertThat(updatedNote.getNoteStatus()).isEqualTo(updateRequest.status()),
                () -> assertThat(updatedNote.getPublisher().getId()).isEqualTo(user.getId()),
                () -> assertThat(updatedNote.getSong().getId()).isEqualTo(song.getId())
        );
    }

    @Test
    void 가사가_없는_노트_수정시에_가사가_생긴_경우_가사가_저장되어야_한다() {
        // given
        User user = userCommandRepository.save(UserFixture.create());

        Artist artist = artistCommandRepository.save(ArtistFixture.create());
        Song song = songCommandRepository.save(SongFixture.create(artist));

        NoteCreateRequest createRequest = new NoteCreateRequest(
                "content",
                null,
                null,
                NoteStatus.DRAFT,
                song.getId()
        );
        Note note = sut.create(createRequest, user.getId());

        NoteUpdateRequest updateRequest = new NoteUpdateRequest(
                "updated content",
                "lyrics",
                NoteBackground.WHITE,
                NoteStatus.PUBLISHED
        );

        // when
        Note updatedNote = sut.update(updateRequest, note.getId(), user.getId());

        // then
        assertAll(
                () -> assertThat(updatedNote.getLyrics().getContent()).isEqualTo(updateRequest.lyrics()),
                () -> assertThat(updatedNote.getLyrics().getBackground()).isEqualTo(updateRequest.background())
        );
    }

    @Test
    void 사용자가_노트의_작성자가_아닌_경우_수정시_예외를_발생시켜야_한다() throws Exception {
        // given
        User publisher = userCommandRepository.save(UserFixture.create());
        User unknownUser = userCommandRepository.save(UserFixture.create());

        Artist artist = artistCommandRepository.save(ArtistFixture.create());
        Song song = songCommandRepository.save(SongFixture.create(artist));

        NoteCreateRequest createRequest = new NoteCreateRequest(
                "content",
                null,
                null,
                NoteStatus.DRAFT,
                song.getId()
        );
        Note note = sut.create(createRequest, publisher.getId());

        NoteUpdateRequest updateRequest = new NoteUpdateRequest(
                "updated content",
                "lyrics",
                NoteBackground.WHITE,
                NoteStatus.PUBLISHED
        );

        // when, then
        assertThatThrownBy(() -> sut.update(updateRequest, note.getId(), unknownUser.getId()))
                .isInstanceOf(InvalidNoteUpdateException.class);
    }
}