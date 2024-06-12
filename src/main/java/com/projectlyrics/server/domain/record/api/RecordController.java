package com.projectlyrics.server.domain.record.api;

import com.projectlyrics.server.domain.common.dto.util.CursorBasePaginatedResponse;
import com.projectlyrics.server.domain.record.dto.request.RecordAddRequest;
import com.projectlyrics.server.domain.record.dto.request.RecordGetResponse;
import com.projectlyrics.server.domain.record.dto.response.RecordAddResponse;
import com.projectlyrics.server.domain.record.service.RecordCommandService;
import com.projectlyrics.server.domain.record.service.RecordQueryService;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/records")
@RestController
public class RecordController {

  private final RecordCommandService recordCommandService;
  private final RecordQueryService recordQueryService;

  @PostMapping
  public ResponseEntity<RecordAddResponse> addRecord(
      Principal principal,
      @RequestBody RecordAddRequest request
  ) {
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(recordCommandService.addRecord(Long.parseLong(principal.getName()), request));
  }

  @DeleteMapping("/{artistId}")
  public ResponseEntity<Void> deleteRecord(
      Principal principal,
      @PathVariable(name = "artistId") Long artistId
  ) {
    recordCommandService.deleteRecord(Long.parseLong(principal.getName()), artistId);

    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .body(null);
  }

  @GetMapping
  public ResponseEntity<CursorBasePaginatedResponse<RecordGetResponse>> getRecordsOfUser(
      Principal principal,
      @RequestParam(required = false) Long cursor,
      @RequestParam(defaultValue = "10") int size
  ) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(recordQueryService.getRecordsByUserId(
            Long.parseLong(principal.getName()),
            cursor,
            PageRequest.of(0, size))
        );
  }

  @GetMapping("/{artistId}")
  public ResponseEntity<RecordGetResponse> getRecord(
      Principal principal,
      @PathVariable(name = "airtistId") Long artistId
  ) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(RecordGetResponse.of(
                recordQueryService.getRecordByUserIdAndArtistId(
                    Long.parseLong(principal.getName()),
                    artistId
                ))
        );
  }
}
