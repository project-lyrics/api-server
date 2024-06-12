package com.projectlyrics.server.domain.artist.api;

import com.projectlyrics.server.domain.artist.dto.request.ArtistAddRequest;
import com.projectlyrics.server.domain.artist.dto.request.ArtistUpdateRequest;
import com.projectlyrics.server.domain.artist.dto.response.ArtistAddResponse;
import com.projectlyrics.server.domain.artist.dto.response.ArtistGetResponse;
import com.projectlyrics.server.domain.artist.dto.response.ArtistUpdateResponse;
import com.projectlyrics.server.domain.common.dto.SuccessResponse;
import com.projectlyrics.server.domain.common.dto.util.CursorBasePaginatedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "아티스트 관련 API")
public interface ArtistControllerSwagger {

  @Operation(
      summary = "아티스트 추가 API",
      description = "아티스트의 데이터를 전달받아 새로운 아티스트를 추가합니다."
  )
  public ResponseEntity<SuccessResponse<ArtistAddResponse>> addArtist(
      @RequestBody ArtistAddRequest request
  );

  @Operation(
      summary = "아티스트 수정 API",
      description = "수정할 아티스트의 PK와 데이터를 전달받아 아티스트 데이터를 수정합니다."
  )
  public ResponseEntity<SuccessResponse<ArtistUpdateResponse>> updateArtist(
      @PathVariable Long artistId,
      @RequestBody ArtistUpdateRequest request
  );

  @Operation(
      summary = "아티스트 삭제 API",
      description = "삭제할 아티스트의 PK를 전달받아 아티스트 데이터를 삭제합니다."
  )
  public ResponseEntity<SuccessResponse<Void>> deleteArtist(
      @PathVariable Long artistId
  );

  @Operation(
      summary = "아티스트 단건 조회 API",
      description = "아티스트의 PK를 전달받아 아티스트 데이터를 조회해 반환합니다."
  )
  public ResponseEntity<SuccessResponse<ArtistGetResponse>> getArtist(
      @PathVariable Long artistId
  );

  @Operation(
      summary = "아티스트 리스트 조회 API",
      description = "아티스트의 데이터의 목록을 조회합니다."
  )
  public ResponseEntity<SuccessResponse<CursorBasePaginatedResponse<ArtistGetResponse>>> getArtistList(
      @Parameter(description = "이전에 응답 받은 nextCursor 값. 응답 받은 값이 없다면 해당 값을 비워서 요청합니다.")
      @RequestParam(required = false) Long cursor,
      @Parameter(description = "조회할 데이터의 최대 크기")
      @RequestParam(defaultValue = "10") int size
  );

  @Operation(
      summary = "아티스트 검색 API",
      description = "사용자로부터 검색어를 전달받아 아티스트 데이터를 조회해 반환합니다."
  )
  public ResponseEntity<SuccessResponse<CursorBasePaginatedResponse<ArtistGetResponse>>> searchArtist(
      @RequestParam(required = false) Long cursor,
      @RequestParam(required = false, defaultValue = "5") int size,
      @RequestParam String query
  );
}
