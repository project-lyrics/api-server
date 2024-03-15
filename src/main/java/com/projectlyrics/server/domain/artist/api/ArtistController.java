package com.projectlyrics.server.domain.artist.api;

import com.projectlyrics.server.domain.artist.dto.request.AddArtistRequest;
import com.projectlyrics.server.domain.artist.dto.request.UpdateArtistRequest;
import com.projectlyrics.server.domain.artist.dto.response.AddArtistResponse;
import com.projectlyrics.server.domain.artist.dto.response.GetArtistResponse;
import com.projectlyrics.server.domain.artist.dto.response.UpdateArtistResponse;
import com.projectlyrics.server.domain.artist.service.ArtistCommandService;
import com.projectlyrics.server.domain.artist.service.ArtistQueryService;
import com.projectlyrics.server.domain.common.dto.CursorBasePaginatedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "아티스트 관련 API")
@RequiredArgsConstructor
@RequestMapping("/api/v1/artists")
@RestController
public class ArtistController {

  private final ArtistQueryService artistQueryService;
  private final ArtistCommandService artistCommandService;

  @Operation(
      summary = "아티스트 추가 API",
      description = "아티스트의 데이터를 전달받아 새로운 아티스트를 추가합니다."
  )
  @PostMapping
  public ResponseEntity<AddArtistResponse> addArtist(@RequestBody @Valid AddArtistRequest request) {
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(artistCommandService.addArtist(request));
  }

  @Operation(
      summary = "아티스트 수정 API",
      description = "수정할 아티스트의 PK와 데이터를 전달받아 아티스트 데이터를 수정합니다."
  )
  @PatchMapping("/{artistId}")
  public ResponseEntity<UpdateArtistResponse> updateArtist(
      @PathVariable Long artistId, @RequestBody UpdateArtistRequest request
  ) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(artistCommandService.updateArtist(artistId, request));
  }

  @Operation(
      summary = "아티스트 삭제 API",
      description = "삭제할 아티스트의 PK를 전달받아 아티스트 데이터를 삭제합니다."
  )
  @DeleteMapping("/{artistId}")
  public ResponseEntity<Void> deleteArtist(@PathVariable Long artistId) {
    artistCommandService.deleteArtist(artistId);

    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .body(null);
  }

  @Operation(
      summary = "아티스트 단건 조회 API",
      description = "아티스트의 PK를 전달받아 아티스트 데이터를 조회해 반환합니다."
  )
  @GetMapping("/{artistId}")
  public ResponseEntity<GetArtistResponse> getArtist(@PathVariable Long artistId) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(artistQueryService.getArtist(artistId));
  }

  @Operation(
      summary = "아티스트 리스트 조회 API",
      description = "아티스트의 데이터의 목록을 조회합니다."
  )
  @GetMapping
  public ResponseEntity<CursorBasePaginatedResponse<GetArtistResponse>> getArtistList(
      @Parameter(description = "페이지 번호 (1부터 시작)") @RequestParam(defaultValue = "1") int page,
      @Parameter(description = "조회할 데이터의 최대 크기") @RequestParam(defaultValue = "10") int size
  ) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(artistQueryService.getArtistList(PageRequest.of(page - 1, size)));
  }
}
