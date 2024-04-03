package com.projectlyrics.server.domain.artist.infra.controller;

import com.projectlyrics.server.domain.artist.application.dto.AddArtistDto;
import com.projectlyrics.server.domain.artist.application.dto.GetSingleArtistMetaDto;
import com.projectlyrics.server.domain.artist.application.usecase.command.AddArtistUseCase;
import com.projectlyrics.server.domain.artist.application.usecase.query.GetSingleArtistByIdMetaUseCase;
import com.projectlyrics.server.domain.common.dto.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Artists")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/artists")
public class ArtistController {

  private final AddArtistUseCase addArtistUseCase;
  private final GetSingleArtistByIdMetaUseCase getSingleArtistByIdMetaUseCase;

  @Operation(
      summary = "아티스트 추가 API",
      description = "아티스트의 데이터를 전달받아 새로운 아티스트를 추가합니다."
  )
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public CommonResponse<AddArtistDto.Response> addArtist(@RequestBody @Valid AddArtistDto.Request request) {
    return new CommonResponse<>(true, HttpStatus.CREATED, addArtistUseCase.addArtist(request));
  }

  @Operation(summary = "단일 아티스트 조회", description = "아티스트의 ID로 단일 아티스트를 조회합니다.")
  @GetMapping("/{artistId}")
  public CommonResponse<GetSingleArtistMetaDto.Response> getSingleArtistMeta(@PathVariable long artistId) {
    return new CommonResponse<>(true, HttpStatus.OK, getSingleArtistByIdMetaUseCase.getMetaById(artistId));
  }
}
