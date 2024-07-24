package com.projectlyrics.server.domain.note.service;

import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.artist.repository.ArtistCommandRepository;
import com.projectlyrics.server.domain.common.dto.util.CursorBasePaginatedResponse;
import com.projectlyrics.server.domain.note.dto.request.NoteCreateRequest;
import com.projectlyrics.server.domain.note.dto.response.NoteGetResponse;
import com.projectlyrics.server.domain.note.entity.Note;
import com.projectlyrics.server.domain.note.entity.NoteBackground;
import com.projectlyrics.server.domain.note.entity.NoteStatus;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class NoteQueryServiceTest extends IntegrationTest {

    @Autowired
    UserCommandRepository userCommandRepository;

    @Autowired
    ArtistCommandRepository artistCommandRepository;

    @Autowired
    SongCommandRepository songCommandRepository;

    @Autowired
    NoteCommandService noteCommandService;

    @Autowired
    NoteQueryService sut;

    @Test
    void 사용자_id와_일치하는_작성자와_연관된_노트_리스트를_조회해야_한다() {
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

        Note note1 = noteCommandService.create(request);
        Note note2 = noteCommandService.create(request);
        Note note3 = noteCommandService.create(request);

        // when
        CursorBasePaginatedResponse<NoteGetResponse> result = sut.getNotesByUserId(user.getId(), null, 5);
        assertAll(
                () -> assertThat(result.data().size()).isEqualTo(3),
                () -> assertThat(result.data().get(0).id()).isEqualTo(note1.getId()),
                () -> assertThat(result.data().get(1).id()).isEqualTo(note2.getId()),
                () -> assertThat(result.data().get(2).id()).isEqualTo(note3.getId())
        );
    }
}