package com.projectlyrics.server.domain.note.service;

import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.artist.repository.ArtistCommandRepository;
import com.projectlyrics.server.domain.note.dto.request.NoteCreateRequest;
import com.projectlyrics.server.domain.note.entity.Note;
import com.projectlyrics.server.domain.note.entity.NoteBackground;
import com.projectlyrics.server.domain.note.entity.NoteStatus;
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

import static org.assertj.core.api.Assertions.assertThat;
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
                user.getId(),
                song.getId()
        );

        // when
        Note note = sut.create(request);

        // then
        Slice<Note> result = noteQueryRepository.findAllByUserId(user.getId(), null, PageRequest.ofSize(5));
        assertAll(
                () -> assertThat(result.getContent().size()).isEqualTo(1),
                () -> assertThat(result.getContent().getFirst().getId()).isEqualTo(note.getId()),
                () -> assertThat(result.getContent().getFirst().getContent()).isEqualTo(note.getContent()),
                () -> assertThat(result.getContent().getFirst().getLyrics()).isEqualTo(note.getLyrics()),
                () -> assertThat(result.getContent().getFirst().getBackground()).isEqualTo(note.getBackground()),
                () -> assertThat(result.getContent().getFirst().getNoteStatus()).isEqualTo(note.getNoteStatus()),
                () -> assertThat(result.getContent().getFirst().getPublisher().getId()).isEqualTo(note.getPublisher().getId()),
                () -> assertThat(result.getContent().getFirst().getSong().getId()).isEqualTo(note.getSong().getId())
        );
    }
}