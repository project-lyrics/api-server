package com.projectlyrics.server.domain.record.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import com.projectlyrics.server.domain.record.repository.RecordQueryRepository;
import com.projectlyrics.server.global.exception.NotFoundException;
import com.projectlyrics.server.utils.ArtistTestUtil;
import com.projectlyrics.server.utils.RecordTestUtil;
import com.projectlyrics.server.utils.UserTestUtil;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
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
    var user = UserTestUtil.create();
    var userId = 1L;

    var artist = ArtistTestUtil.createWithName("검정치마");
    var artistId = 1L;

    var record = RecordTestUtil.create(user, artist);

    given(recordQueryRepository.findByUserIdAndArtistIdAndNotDeleted(userId, artistId))
        .willReturn(Optional.of(record));

    // when
    var recordGetResponse = sut.getRecordByUserIdAndArtistId(userId, artistId);

    // then
    then(recordQueryRepository).should().findByUserIdAndArtistIdAndNotDeleted(anyLong(), anyLong());
    assertThat(recordGetResponse).isEqualTo(record);
  }

  @Test
  void 회원_아이디_혹은_아티스트_아이디로부터_존재하지_않는_레코드에_대해_예외를_발생시킨다() {
    // given
    var userId = 1L;
    var artistId = 1L;

    given(recordQueryRepository.findByUserIdAndArtistIdAndNotDeleted(userId, artistId))
        .willReturn(Optional.empty());

    // when, then
    assertThatThrownBy(() -> sut.getRecordByUserIdAndArtistId(userId, artistId))
        .isInstanceOf(NotFoundException.class);
    then(recordQueryRepository).should().findByUserIdAndArtistIdAndNotDeleted(anyLong(), anyLong());
  }

  @Test
  void 회원_아이디로부터_회원이_등록해둔_모든_레코드를_조회한다() {
    // given
    var user = UserTestUtil.create();
    var userId = 1L;

    var artist1 = ArtistTestUtil.createWithName("검정치마");
    var artist2 = ArtistTestUtil.createWithName("초록불꽃소년단");

    var record1 = spy(RecordTestUtil.create(user, artist1));
    var record2 = spy(RecordTestUtil.create(user, artist2));

    var records = List.of(record1, record2);
    var cursor = 5L;
    var pageable = PageRequest.of(0, 2);
    given(recordQueryRepository.findAllByUserIdAndNotDeleted(userId, cursor, pageable))
        .willReturn(new SliceImpl<>(records, pageable, true));
    doReturn(5L).when(record1).getId();
    doReturn(6L).when(record2).getId();

    // when
    var recordGetAllByUserResponse = sut.getRecordsByUserId(userId, cursor, pageable);

    // then
    then(recordQueryRepository).should().findAllByUserIdAndNotDeleted(userId, cursor, pageable);
    assertThat(recordGetAllByUserResponse.currentCursor()).isEqualTo(String.valueOf(cursor));
    assertThat(recordGetAllByUserResponse.nextCursor()).isEqualTo(String.valueOf(record2.getId() + 1));
    assertThat(recordGetAllByUserResponse.itemSize()).isEqualTo(records.size());
    assertThat(recordGetAllByUserResponse.totalSize()).isEqualTo(pageable.getPageSize());
  }
}