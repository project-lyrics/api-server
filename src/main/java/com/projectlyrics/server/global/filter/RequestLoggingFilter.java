package com.projectlyrics.server.global.filter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        if (request.getRequestURI().startsWith("/docs")) {
            chain.doFilter(request, response);
            return;
        }
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        long start = System.currentTimeMillis();
        chain.doFilter(requestWrapper, responseWrapper);
        long end = System.currentTimeMillis();

        if (responseWrapper.getStatus() != HttpStatus.OK.value()) {
            log.info("""
                            |
                            | [REQUEST] {} {} {} ({}s)
                            | Headers : {}
                            | Request : {}
                            | Response : {}
                            """.trim(),
                    request.getMethod(),
                    request.getRequestURI(),
                    HttpStatus.valueOf(responseWrapper.getStatus()),
                    (end - start) / 1000.0,
                    getHeaders(request),
                    getRequestBody(requestWrapper),
                    getResponseBody(responseWrapper)
            );
        }
    }

    private String getHeaders(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder("{\n");

        Enumeration<String> headerArray = request.getHeaderNames();
        while (headerArray.hasMoreElements()) {
            String headerName = headerArray.nextElement();
            sb.append("\t%s=%s,\n".formatted(headerName, request.getHeader(headerName)));
        }
        sb.append("}");
        return sb.toString();
    }

    private String getRequestBody(ContentCachingRequestWrapper request) {
        ContentCachingRequestWrapper wrapper = WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);
        assert wrapper != null;

        byte[] buf = wrapper.getContentAsByteArray();
        if (buf.length > 0) {
            try {
                return new String(buf, wrapper.getCharacterEncoding());
            } catch (UnsupportedEncodingException e) {
                return "";
            }
        }
        return "";
    }

    private String getResponseBody(final HttpServletResponse response) throws IOException {
        String payload = null;
        ContentCachingResponseWrapper wrapper =
                WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
        assert wrapper != null;

        byte[] buf = wrapper.getContentAsByteArray();
        if (buf.length > 0) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String responseBody = new String(buf, StandardCharsets.UTF_8);
            JsonElement jsonElement = JsonParser.parseString(responseBody);
            payload = gson.toJson(jsonElement);
        }
        wrapper.copyBodyToResponse();
        return Objects.isNull(payload) ? "" : payload;
    }
}
