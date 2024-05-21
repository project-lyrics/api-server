package com.projectlyrics.server.domain.artist.service;

import com.projectlyrics.server.domain.artist.dto.response.ArtistGetResponse;
import com.projectlyrics.server.domain.common.dto.util.CursorBasePaginatedResponse;
import org.springframework.data.domain.Pageable;

public interface ArtistQueryService {

  ArtistGetResponse getArtist(Long artistId);

  CursorBasePaginatedResponse<ArtistGetResponse> getArtistList(Long cursor, Pageable pageable);
}
