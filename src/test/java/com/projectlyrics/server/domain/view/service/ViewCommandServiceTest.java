package com.projectlyrics.server.domain.view.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.artist.repository.ArtistCommandRepository;
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
import com.projectlyrics.server.domain.view.domain.View;
import com.projectlyrics.server.domain.view.repository.ViewQueryRepository;
import com.projectlyrics.server.support.IntegrationTest;
import com.projectlyrics.server.support.fixture.ArtistFixture;
import com.projectlyrics.server.support.fixture.SongFixture;
import com.projectlyrics.server.support.fixture.UserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ViewCommandServiceTest extends IntegrationTest {

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
    ViewQueryRepository viewQueryRepository;

    @Autowired
    ViewCommandService sut;

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
    void 조회수를_발행해야_한다() {
        // given
        String deviceId = "DEVICE_ID";

        // when
        View view = sut.create(note.getId(), user.getId(), deviceId);

        // then
        View result = viewQueryRepository.findById(view.getId());
        assertAll(
                () -> assertThat(result.getNote().getId()).isEqualTo(view.getNote().getId()),
                () -> assertThat(result.getDeviceId()).isEqualTo(view.getDeviceId()),
                () -> assertThat(result.getUser().getId()).isEqualTo(view.getUser().getId())
        );
    }

    @Test
    void 유저id_없이도_조회수를_발행해야_한다() {
        // given
        String deviceId = "DEVICE_ID";

        // when
        View view = sut.create(note.getId(), deviceId);

        // then
        View result = viewQueryRepository.findById(view.getId());
        assertAll(
                () -> assertThat(result.getNote().getId()).isEqualTo(view.getNote().getId()),
                () -> assertThat(result.getDeviceId()).isEqualTo(view.getDeviceId()),
                () -> assertThat(result.getUser()).isNull()
        );
    }
}
