package com.projectlyrics.server.domain.record.service;

import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.artist.service.ArtistQueryService;
import com.projectlyrics.server.domain.record.domain.Record;
import com.projectlyrics.server.domain.record.dto.request.RecordAddRequest;
import com.projectlyrics.server.domain.record.dto.response.RecordAddResponse;
import com.projectlyrics.server.domain.record.repository.RecordCommandRepository;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.service.UserQueryService;
import java.time.Clock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class RecordCommandService {

  private final RecordCommandRepository recordCommandRepository;
  private final RecordQueryService recordQueryService;
  private final UserQueryService userQueryService;
  private final ArtistQueryService artistQueryService;

  public RecordAddResponse addRecord(long userId, RecordAddRequest request) {
    User user = userQueryService.getUserById(userId);
    Artist artist = artistQueryService.getArtistById(request.artistId());

    Record record = recordCommandRepository.save(request.toEntity(artist, user));
    return RecordAddResponse.of(record);
  }

  public void deleteRecord(long userId, long artistId) {
    Record record = recordQueryService.getRecordByUserIdAndArtistId(userId, artistId);

    record.delete(userId, Clock.systemDefaultZone());
  }
}
