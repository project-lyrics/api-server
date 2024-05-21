package com.projectlyrics.server.domain.artist.service;

import com.projectlyrics.server.domain.artist.dto.response.GetArtistResponse;
import com.projectlyrics.server.domain.common.dto.util.CursorBasePaginatedResponse;
import org.springframework.data.domain.Pageable;

public interface ArtistQueryService {

  GetArtistResponse getArtist(Long artistId);

  CursorBasePaginatedResponse<GetArtistResponse> getArtistList(Long cursor, Pageable pageable);
}
