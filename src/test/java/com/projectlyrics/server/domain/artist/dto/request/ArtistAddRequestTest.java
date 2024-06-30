package com.projectlyrics.server.domain.artist.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ArtistAddRequestTest {

    private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @ParameterizedTest
    @ValueSource(strings = {"", "     "})
    void 아티스트의_이름이_빈_문자열이나_공백_문자열이라면_validation_에러가_발생한다(String name) {
        // given
        ArtistAddRequest addArtistRequest = createAddArtistRequest(name, "https://~");

        // when
        Set<ConstraintViolation<ArtistAddRequest>> violation = validator.validate(addArtistRequest);

        // then
        violation.forEach(error -> {
            assertThat(error.getMessage()).isEqualTo("공백일 수 없습니다");
        });
    }

    private ArtistAddRequest createAddArtistRequest(String name, String profileImageCdnLink) {
        return new ArtistAddRequest(name, profileImageCdnLink);
    }
}