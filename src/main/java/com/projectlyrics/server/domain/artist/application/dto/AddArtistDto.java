package com.projectlyrics.server.domain.artist.application.dto;

import com.projectlyrics.server.domain.artist.entity.ArtistEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;

public class AddArtistDto {

  public record Request(
      @Schema(name = "아티스트의 한글 이름")
      @Pattern(regexp = "^(?!\\s*$).+", message = "빈 문자열 또는 공백 문자열은 허용하지 않습니다.")
      String name,
      @Schema(name = "아티스트의 영어 이름")
      @Pattern(regexp = "^(?!\\s*$).+", message = "빈 문자열 또는 공백 문자열은 허용하지 않습니다.")
      String englishName,
      @Schema(name = "아티스트의 cdn 이미지 경로")
      @Pattern(regexp = "^https://.*", message = "이미지 경로는 https://로 시작해야 합니다.")
      String profileImageCdnLink
  ) {

    public ArtistEntity toEntity() {
      return new ArtistEntity(this.name, this.englishName, this.profileImageCdnLink);
    }
  }

  public record Response(
      @Schema(description = "생성된 아티스트의 id")
      long id
  ) {
  }
}
