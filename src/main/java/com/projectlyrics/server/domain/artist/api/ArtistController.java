package com.projectlyrics.server.domain.artist.api;

import com.projectlyrics.server.domain.artist.dto.request.AddArtistRequest;
import com.projectlyrics.server.domain.artist.dto.request.UpdateArtistRequest;
import com.projectlyrics.server.domain.artist.dto.response.AddArtistResponse;
import com.projectlyrics.server.domain.artist.dto.response.GetArtistResponse;
import com.projectlyrics.server.domain.artist.dto.response.UpdateArtistResponse;
import com.projectlyrics.server.domain.artist.service.ArtistCommandService;
import com.projectlyrics.server.domain.artist.service.ArtistQueryService;
import com.projectlyrics.server.domain.common.dto.util.CursorBasePaginatedResponse;
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

@RequiredArgsConstructor
@RequestMapping("/api/v1/artists")
@RestController
public class ArtistController implements ArtistControllerSwagger {

  private final ArtistQueryService artistQueryService;
  private final ArtistCommandService artistCommandService;

  @PostMapping
  public ResponseEntity<AddArtistResponse> addArtist(
      @RequestBody @Valid AddArtistRequest request
  ) {
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(artistCommandService.addArtist(request));
  }

  @PatchMapping("/{artistId}")
  public ResponseEntity<UpdateArtistResponse> updateArtist(
      @PathVariable Long artistId, @RequestBody UpdateArtistRequest request
  ) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(artistCommandService.updateArtist(artistId, request));
  }

  @DeleteMapping("/{artistId}")
  public ResponseEntity<Void> deleteArtist(@PathVariable Long artistId) {
    artistCommandService.deleteArtist(artistId);

    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .body(null);
  }

  @GetMapping("/{artistId}")
  public ResponseEntity<GetArtistResponse> getArtist(@PathVariable Long artistId) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(artistQueryService.getArtist(artistId));
  }

  @GetMapping
  public ResponseEntity<CursorBasePaginatedResponse<GetArtistResponse>> getArtistList(
      @RequestParam(required = false) Long cursor,
      @RequestParam(defaultValue = "10") int size
  ) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(artistQueryService.getArtistList(cursor, PageRequest.of(0, size)));
  }
}
