package com.projectlyrics.server.domain.artist.service.impl;

import com.projectlyrics.server.domain.artist.dto.request.AddArtistRequest;
import com.projectlyrics.server.domain.artist.dto.request.UpdateArtistRequest;
import com.projectlyrics.server.domain.artist.dto.response.AddArtistResponse;
import com.projectlyrics.server.domain.artist.dto.response.UpdateArtistResponse;
import com.projectlyrics.server.domain.artist.repository.CommandQueryArtistRepository;
import com.projectlyrics.server.domain.artist.service.ArtistService;
import com.projectlyrics.server.global.error_code.ErrorCode;
import com.projectlyrics.server.global.exception.BusinessException;
import java.time.Clock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ArtistServiceImpl implements ArtistService {

  private final CommandQueryArtistRepository commandArtistRepository;

  @Transactional
  @Override
  public AddArtistResponse addArtist(AddArtistRequest request) {
    var savedArtist = commandArtistRepository.save(request.toEntity());
    return AddArtistResponse.of(savedArtist.getId());
  }

  @Transactional
  @Override
  public UpdateArtistResponse updateArtist(Long artistId, UpdateArtistRequest request) {
    var artist = commandArtistRepository.findByIdAndNotDeleted(artistId)
        .orElseThrow(() -> new BusinessException(ErrorCode.ARTIST_NOT_FOUND));

    artist.updateIfNotBlank(request.name(), artist::updateName);
    artist.updateIfNotBlank(request.englishName(), artist::updateEnglishName);
    artist.updateIfNotBlank(request.profileImageCdnLink(), artist::updateProfileImageCdnLink);

    return UpdateArtistResponse.from(artist);
  }

  @Transactional
  @Override
  public void deleteArtist(Long artistId) {
    var artist = commandArtistRepository.findByIdAndNotDeleted(artistId)
        .orElseThrow(() -> new BusinessException(ErrorCode.ARTIST_NOT_FOUND));

    // TODO: 인증 구현되면 deletedById 값 수정
    artist.getCommonField().delete(1L, Clock.systemDefaultZone());
  }
}
