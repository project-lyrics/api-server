package com.projectlyrics.server.domain.artist.api;

import com.projectlyrics.server.domain.artist.dto.request.AddArtistRequest;
import com.projectlyrics.server.domain.artist.dto.response.AddArtistResponse;
import com.projectlyrics.server.domain.artist.service.ArtistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    var artistId = artistService.addArtist(request);

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(artistId);
  }
}
