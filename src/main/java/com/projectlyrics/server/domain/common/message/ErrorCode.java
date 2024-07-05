package com.projectlyrics.server.domain.common.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "00000", "Invalid request."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "00001", "일부 필드가 잘못된 값으로 입력되었습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "00002", "서버 내부에 오류가 발생했습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "00003", "Not Found"),
    NULL_FIELD(HttpStatus.BAD_REQUEST, "00004", "Some field is missing."),
    EMPTY_FIELD(HttpStatus.BAD_REQUEST, "00005", "Some field is empty."),
    INVALID_URL_PREFIX(HttpStatus.BAD_REQUEST, "00006", "URL should start with http:// or https://."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "00007", "Resource not found."),
    INVALID_REQUEST_FIELD(HttpStatus.BAD_REQUEST, "00008", "Some request field is not valid."),


    // Auth
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "01000", "The refresh token could not be found."),
    TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "01001", "The token is expired."),
    WRONG_TOKEN_TYPE(HttpStatus.BAD_REQUEST, "01002", "잘못된 토큰 형식으로 입력되었습니다."),
    UNSUPPORTED_AUTH_PROVIDER(HttpStatus.BAD_REQUEST, "01003", "It is unsupported authentication provider"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "01004", "토큰이 유효하지 않습니다."),
    INVALID_KEY(HttpStatus.UNAUTHORIZED, "01005", "The key is not valid."),
    INVALID_AUTH_SECRET_KEY(HttpStatus.UNAUTHORIZED, "01006", "The secret key is not valid."),
    NOT_AGREE_TO_TERMS(HttpStatus.BAD_REQUEST, "01007", "Not agree to terms and conditions"),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "02000", "The user data could not be found."),
    INVALID_USERNAME(HttpStatus.BAD_REQUEST, "02001", "The username must be at least one character long and include letters, numbers, or a combination of both."),
    INVALID_AGE(HttpStatus.BAD_REQUEST, "02002", "The age must 14 years or older"),

    // Artist,
    ARTIST_NOT_FOUND(HttpStatus.NOT_FOUND, "03000", "The artist data could not be found."),
    ARTIST_UPDATE_NOT_VALID(HttpStatus.BAD_REQUEST, "03001", "The data to be updated failed validation."),

    // Record,
    RECORD_NOT_FOUND(HttpStatus.NOT_FOUND, "04000", "The record data could not be found.");

    private final HttpStatus responseStatus;
    private final String errorCode;
    private final String errorMessage;
}
