package com.projectlyrics.server.domain.artist.api;

import com.projectlyrics.server.domain.artist.dto.request.AddArtistRequest;
import com.projectlyrics.server.domain.artist.dto.request.UpdateArtistRequest;
import com.projectlyrics.server.domain.artist.dto.response.AddArtistResponse;
import com.projectlyrics.server.domain.artist.dto.response.GetArtistResponse;
import com.projectlyrics.server.domain.artist.dto.response.UpdateArtistResponse;
import com.projectlyrics.server.domain.common.dto.CursorBasePaginatedResponse;
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
  public ResponseEntity<AddArtistResponse> addArtist(
      @RequestBody AddArtistRequest request
  );

  @Operation(
      summary = "아티스트 수정 API",
      description = "수정할 아티스트의 PK와 데이터를 전달받아 아티스트 데이터를 수정합니다."
  )
  public ResponseEntity<UpdateArtistResponse> updateArtist(
      @PathVariable Long artistId,
      @RequestBody UpdateArtistRequest request
  );

  @Operation(
      summary = "아티스트 삭제 API",
      description = "삭제할 아티스트의 PK를 전달받아 아티스트 데이터를 삭제합니다."
  )
  public ResponseEntity<Void> deleteArtist(
      @PathVariable Long artistId
  );

  @Operation(
      summary = "아티스트 단건 조회 API",
      description = "아티스트의 PK를 전달받아 아티스트 데이터를 조회해 반환합니다."
  )
  public ResponseEntity<GetArtistResponse> getArtist(
      @PathVariable Long artistId
  );

  @Operation(
      summary = "아티스트 리스트 조회 API",
      description = "아티스트의 데이터의 목록을 조회합니다."
  )
  public ResponseEntity<CursorBasePaginatedResponse<GetArtistResponse>> getArtistList(
      @Parameter(description = "이전에 응답 받은 nextCursor 값. 응답 받은 값이 없다면 해당 값을 비워서 요청합니다.")
      @RequestParam(required = false) Long cursor,
      @Parameter(description = "조회할 데이터의 최대 크기")
      @RequestParam(defaultValue = "10") int size
  );
}
