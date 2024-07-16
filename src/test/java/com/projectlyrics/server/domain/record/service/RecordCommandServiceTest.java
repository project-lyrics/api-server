package com.projectlyrics.server.domain.record.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.artist.service.ArtistQueryService;
import com.projectlyrics.server.domain.record.domain.Record;
import com.projectlyrics.server.domain.record.dto.request.RecordAddRequest;
import com.projectlyrics.server.domain.record.repository.RecordCommandRepository;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.service.UserQueryService;
import com.projectlyrics.server.support.fixture.ArtistFixture;
import com.projectlyrics.server.support.fixture.RecordFixture;
import com.projectlyrics.server.support.fixture.UserFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RecordCommandServiceTest {

    @InjectMocks
    private RecordCommandService sut;

    @Mock
    private RecordCommandRepository recordCommandRepository;

    @Mock
    private ArtistQueryService artistQueryService;

    @Mock
    private UserQueryService userQueryService;

    @Captor
    private ArgumentCaptor<Record> addRecordArgumentCaptor;

    @Test
    void 전달받은_데이터로_새로운_레코드를_추가한다() {
        // given
        long artistId = 1L;
        Artist artist = ArtistFixture.createWithName("검정치마");
        given(artistQueryService.getArtistById(artistId)).willReturn(artist);

        long userId = 1L;
        User user = UserFixture.create();
        given(userQueryService.getUserById(userId)).willReturn(user);

        given(recordCommandRepository.save(any(Record.class))).willReturn(RecordFixture.create(user, artist));

        // when
        sut.addRecord(userId, new RecordAddRequest(artistId));

        // then
        then(recordCommandRepository).should().save(addRecordArgumentCaptor.capture());
        Record captorValue = addRecordArgumentCaptor.getValue();

        assertThat(captorValue.getArtist()).isEqualTo(artist);
        assertThat(captorValue.getUser()).isEqualTo(user);
    }
}
