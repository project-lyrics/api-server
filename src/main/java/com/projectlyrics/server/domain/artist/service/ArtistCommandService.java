package com.projectlyrics.server.domain.artist.service;

import com.projectlyrics.server.domain.artist.dto.request.ArtistAddRequest;
import com.projectlyrics.server.domain.artist.dto.request.ArtistUpdateRequest;
import com.projectlyrics.server.domain.artist.dto.response.ArtistAddResponse;
import com.projectlyrics.server.domain.artist.dto.response.ArtistUpdateResponse;
import com.projectlyrics.server.domain.artist.repository.CommandArtistRepository;
import com.projectlyrics.server.domain.artist.repository.QueryArtistRepository;
import com.projectlyrics.server.global.exception.FeelinException;
import com.projectlyrics.server.domain.common.message.ErrorCode;
import java.time.Clock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class ArtistCommandService {

  private final CommandArtistRepository commandArtistRepository;
  private final QueryArtistRepository queryArtistRepository;
  private final Clock clock;

  public ArtistAddResponse addArtist(ArtistAddRequest request) {
    var savedArtist = commandArtistRepository.save(request.toEntity());
    return ArtistAddResponse.of(savedArtist.getId());
  }

  public ArtistUpdateResponse updateArtist(Long artistId, ArtistUpdateRequest request) {
    var artist = queryArtistRepository.findByIdAndNotDeleted(artistId)
        .orElseThrow(() -> new FeelinException(ErrorCode.ARTIST_NOT_FOUND));

    artist.updateIfNotBlank(request.name(), artist::updateName);
    artist.updateIfNotBlank(request.englishName(), artist::updateEnglishName);
    artist.updateIfNotBlank(request.profileImageCdnLink(), artist::updateProfileImageCdnLink);

    return ArtistUpdateResponse.from(artist);
  }

  public void deleteArtist(Long artistId) {
    var artist = queryArtistRepository.findByIdAndNotDeleted(artistId)
        .orElseThrow(() -> new FeelinException(ErrorCode.ARTIST_NOT_FOUND));

    // TODO: 인증 구현되면 deletedById 값 수정
    artist.getCommonField().delete(1L, clock.systemDefaultZone());
  }
}
