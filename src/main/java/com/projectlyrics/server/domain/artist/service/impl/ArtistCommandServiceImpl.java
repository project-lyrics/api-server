package com.projectlyrics.server.domain.artist.service.impl;

import com.projectlyrics.server.domain.artist.dto.request.AddArtistRequest;
import com.projectlyrics.server.domain.artist.dto.request.UpdateArtistRequest;
import com.projectlyrics.server.domain.artist.dto.response.AddArtistResponse;
import com.projectlyrics.server.domain.artist.dto.response.UpdateArtistResponse;
import com.projectlyrics.server.domain.artist.repository.CommandArtistRepository;
import com.projectlyrics.server.domain.artist.repository.QueryArtistRepository;
import com.projectlyrics.server.domain.artist.service.ArtistCommandService;
import com.projectlyrics.server.global.exception.FeelinException;
import com.projectlyrics.server.domain.common.message.ErrorCode;
import java.time.Clock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class ArtistCommandServiceImpl implements ArtistCommandService {

  private final CommandArtistRepository commandArtistRepository;
  private final QueryArtistRepository queryArtistRepository;
  private final Clock clock;

  @Override
  public AddArtistResponse addArtist(AddArtistRequest request) {
    var savedArtist = commandArtistRepository.save(request.toEntity());
    return AddArtistResponse.of(savedArtist.getId());
  }

  @Override
  public UpdateArtistResponse updateArtist(Long artistId, UpdateArtistRequest request) {
    var artist = queryArtistRepository.findByIdAndNotDeleted(artistId)
        .orElseThrow(() -> new FeelinException(ErrorCode.ARTIST_NOT_FOUND));

    artist.updateIfNotBlank(request.name(), artist::updateName);
    artist.updateIfNotBlank(request.englishName(), artist::updateEnglishName);
    artist.updateIfNotBlank(request.profileImageCdnLink(), artist::updateProfileImageCdnLink);

    return UpdateArtistResponse.from(artist);
  }

  @Override
  public void deleteArtist(Long artistId) {
    var artist = queryArtistRepository.findByIdAndNotDeleted(artistId)
        .orElseThrow(() -> new FeelinException(ErrorCode.ARTIST_NOT_FOUND));

    // TODO: 인증 구현되면 deletedById 값 수정
    artist.getCommonField().delete(1L, clock.systemDefaultZone());
  }
}
