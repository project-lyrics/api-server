package com.projectlyrics.server.domain.record.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.common.dto.util.CursorBasePaginatedResponse;
import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.domain.record.domain.Record;
import com.projectlyrics.server.domain.record.dto.request.RecordGetResponse;
import com.projectlyrics.server.domain.record.exception.RecordNotFoundException;
import com.projectlyrics.server.domain.record.repository.RecordQueryRepository;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.global.exception.NotFoundException;
import com.projectlyrics.server.common.fixture.ArtistFixture;
import com.projectlyrics.server.common.fixture.RecordFixture;
import com.projectlyrics.server.common.fixture.UserFixture;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;

@ExtendWith(MockitoExtension.class)
class RecordQueryServiceTest {

    @InjectMocks
    private RecordQueryService sut;

    @Mock
    private RecordQueryRepository recordQueryRepository;

    @Test
    void 회원_아이디와_아티스트_아이디로부터_알맞은_레코드를_반환한다() {
        // given
        User user = UserFixture.create();
        long userId = 1L;

        Artist artist = ArtistFixture.createWithName("검정치마");
        long artistId = 1L;

        Record record = RecordFixture.create(user, artist);

        given(recordQueryRepository.findByUserIdAndArtistIdAndNotDeleted(userId, artistId))
                .willReturn(Optional.of(record));

        // when
        Record recordGetResponse = sut.getRecordByUserIdAndArtistId(userId, artistId);

        // then
        then(recordQueryRepository).should().findByUserIdAndArtistIdAndNotDeleted(anyLong(), anyLong());
        assertThat(recordGetResponse).isEqualTo(record);
    }

    @Test
    void 회원_아이디_혹은_아티스트_아이디로부터_존재하지_않는_레코드에_대해_예외를_발생시킨다() {
        // given
        long userId = 1L;
        long artistId = 1L;

        given(recordQueryRepository.findByUserIdAndArtistIdAndNotDeleted(userId, artistId))
                .willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> sut.getRecordByUserIdAndArtistId(userId, artistId))
                .isInstanceOf(RecordNotFoundException.class)
                .hasMessage(ErrorCode.RECORD_NOT_FOUND.getErrorMessage());
        then(recordQueryRepository).should().findByUserIdAndArtistIdAndNotDeleted(anyLong(), anyLong());
    }

    @Test
    void 회원_아이디로부터_회원이_등록해둔_모든_레코드를_조회한다() {
        // given
        User user = UserFixture.create();
        long userId = 1L;

        Artist artist1 = ArtistFixture.createWithName("검정치마");
        Artist artist2 = ArtistFixture.createWithName("초록불꽃소년단");

        Record record1 = spy(RecordFixture.create(user, artist1));
        Record record2 = spy(RecordFixture.create(user, artist2));

        List<Record> records = List.of(record1, record2);
        long cursor = 5L;
        Pageable pageable = PageRequest.of(0, 2);
        given(recordQueryRepository.findAllByUserIdAndNotDeleted(userId, cursor, pageable))
                .willReturn(new SliceImpl<>(records, pageable, true));
        doReturn(5L).when(record1).getId();
        doReturn(6L).when(record2).getId();

        // when
        CursorBasePaginatedResponse<RecordGetResponse> recordGetAllByUserResponse = sut.getRecordsByUserId(userId, cursor, pageable);

        // then
        then(recordQueryRepository).should().findAllByUserIdAndNotDeleted(userId, cursor, pageable);
        assertThat(recordGetAllByUserResponse.hasNext()).isTrue();
        assertThat(recordGetAllByUserResponse.data().size()).isEqualTo(records.size());
    }
}