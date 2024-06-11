package com.projectlyrics.server.domain.artist.api;

import com.projectlyrics.server.domain.artist.dto.request.ArtistAddRequest;
import com.projectlyrics.server.domain.artist.dto.request.ArtistUpdateRequest;
import com.projectlyrics.server.domain.artist.dto.response.ArtistAddResponse;
import com.projectlyrics.server.domain.artist.dto.response.ArtistGetResponse;
import com.projectlyrics.server.domain.artist.dto.response.ArtistUpdateResponse;
import com.projectlyrics.server.domain.artist.service.ArtistCommandService;
import com.projectlyrics.server.domain.artist.service.ArtistQueryService;
import com.projectlyrics.server.domain.common.dto.SuccessResponse;
import com.projectlyrics.server.domain.common.dto.util.CursorBasePaginatedResponse;
import com.projectlyrics.server.domain.common.message.SuccessMessage;
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
  public ResponseEntity<SuccessResponse<ArtistAddResponse>> addArtist(
      @RequestBody @Valid ArtistAddRequest request
  ) {
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(SuccessResponse.of(
            SuccessMessage.ARTIST_ADD_SUCCESS,
            artistCommandService.addArtist(request)
        ));
  }

  @PatchMapping("/{artistId}")
  public ResponseEntity<SuccessResponse<ArtistUpdateResponse>> updateArtist(
      @PathVariable Long artistId,
      @RequestBody ArtistUpdateRequest request
  ) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(SuccessResponse.of(
            SuccessMessage.ARTIST_UPDATE_SUCCESS,
            artistCommandService.updateArtist(artistId, request)
        ));
  }

  @DeleteMapping("/{artistId}")
  public ResponseEntity<SuccessResponse<Void>> deleteArtist(
      @PathVariable Long artistId
  ) {
    artistCommandService.deleteArtist(artistId);

    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .body(SuccessResponse.of(
            SuccessMessage.ARTIST_DELETE_SUCCESS
        ));
  }

  @GetMapping("/{artistId}")
  public ResponseEntity<SuccessResponse<ArtistGetResponse>> getArtist(
      @PathVariable Long artistId
  ) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(SuccessResponse.of(
            SuccessMessage.ARTIST_GET_SUCCESS,
            ArtistGetResponse.of(artistQueryService.getArtistById(artistId))
        ));
  }

  @GetMapping
  public ResponseEntity<SuccessResponse<CursorBasePaginatedResponse<ArtistGetResponse>>> getArtistList(
      @RequestParam(required = false) Long cursor,
      @RequestParam(defaultValue = "10") int size
  ) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(SuccessResponse.of(
            SuccessMessage.ARTIST_GET_LIST_SUCCESS,
            artistQueryService.getArtistList(cursor, PageRequest.of(0, size))
        ));
  }

  @GetMapping("/search")
  public ResponseEntity<SuccessResponse<CursorBasePaginatedResponse<ArtistGetResponse>>> searchArtist(
      @RequestParam(required = false) Long cursor,
      @RequestParam(required = false, defaultValue = "5") int size,
      @RequestParam String query
  ) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(SuccessResponse.of(
            SuccessMessage.ARTIST_GET_LIST_BY_NAME_SUCCESS,
                artistQueryService.searchArtists(query, cursor, PageRequest.of(0, size))
        ));
  }
}
