package com.projectlyrics.server.global.handler;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectlyrics.server.domain.common.dto.ErrorResponse;
import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.global.exception.FeelinException;
import com.projectlyrics.server.global.exception.JwtValidationException;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class FilterExceptionHandler {

    private final ObjectMapper objectMapper;

    public void handleFilterException(Exception e, HttpServletResponse response) throws IOException {
        if (e instanceof FeelinException exception) {
            setErrorResponse(exception.getErrorCode().getResponseStatus(), response, exception.getErrorCode());
        }
    }

    private void setErrorResponse(HttpStatus httpStatus, HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(httpStatus.value());
        response.getWriter().write(objectMapper.writeValueAsString(ErrorResponse.of(errorCode)));
    }
}
