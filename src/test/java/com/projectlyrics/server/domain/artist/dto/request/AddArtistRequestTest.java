package com.projectlyrics.server.domain.artist.dto.request;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class AddArtistRequestTest {

  private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @ParameterizedTest
  @ValueSource(strings = {"", "     "})
  void 아티스트의_이름이_빈_문자열이나_공백_문자열이라면_validation_에러가_발생한다(String name) {
    // given
    var addArtistRequest = createAddArtistRequest(name, "NELL", "https://~");

    // when
    var violation = validator.validate(addArtistRequest);

    // then
    violation.forEach(error -> {
      assertThat(error.getMessage()).isEqualTo("빈 문자열 또는 공백 문자열은 허용하지 않습니다.");
    });
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "     "})
  void 아티스트의_영어_이름이_문자열이나_빈_문자열_또는_공백_문자열이라면_validation_에러가_발생한다(String englishName) {
    // given
    var addArtistRequest = createAddArtistRequest("넬", englishName, "https://~");

    // when
    var violation = validator.validate(addArtistRequest);

    // then
    violation.forEach(error -> {
      assertThat(error.getMessage()).isEqualTo("빈 문자열 또는 공백 문자열은 허용하지 않습니다.");
    });
  }

  @Test
  void 아티스트의_이미지_경로가_https로_시작하지_않는_문자열이라면_validation_에러가_발생한다() {
    // given
    var addArtistRequest = createAddArtistRequest("넬", "NELL", "imageUrl");

    // when
    var violation = validator.validate(addArtistRequest);

    // then
    violation.forEach(error -> {
      assertThat(error.getMessage()).isEqualTo("이미지 경로는 https://로 시작해야 합니다.");
    });
  }

  private AddArtistRequest createAddArtistRequest(String name, String englishName, String profileImageCdnLink) {
    return new AddArtistRequest(name, englishName, profileImageCdnLink);
  }
}