package com.projectlyrics.server.domain.artist.api;

import com.projectlyrics.server.domain.artist.dto.request.AddArtistRequest;
import com.projectlyrics.server.domain.artist.dto.request.UpdateArtistRequest;
import com.projectlyrics.server.domain.artist.dto.response.AddArtistResponse;
import com.projectlyrics.server.domain.artist.dto.response.UpdateArtistResponse;
import com.projectlyrics.server.domain.artist.service.ArtistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "아티스트 관련 API")
@RequiredArgsConstructor
@RequestMapping("/api/v1/artists")
@RestController
public class ArtistController {

  private final ArtistService artistService;

  @Operation(
      summary = "아티스트 추가 API",
      description = "아티스트의 데이터를 전달받아 새로운 아티스트를 추가합니다."
  )
  @PostMapping
  public ResponseEntity<AddArtistResponse> addArtist(@RequestBody @Valid AddArtistRequest request) {
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(artistService.addArtist(request));
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
        .body(artistService.updateArtist(artistId, request));
  }

  @Operation(
      summary = "아티스트 삭제 API",
      description = "삭제할 아티스트의 PK를 전달받아 아티스트 데이터를 삭제합니다."
  )
  @DeleteMapping("/{artistId}")
  public ResponseEntity<Void> deleteArtist(@PathVariable Long artistId) {
    artistService.deleteArtist(artistId);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(null);
  }
}
