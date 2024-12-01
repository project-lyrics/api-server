package com.projectlyrics.server.global.slack.interceptor;

import com.projectlyrics.server.global.slack.exception.FailedToMatchSlackSignatureException;
import com.projectlyrics.server.global.slack.exception.FailedToReadSlackRequestException;
import com.projectlyrics.server.global.slack.exception.SlackInvalidRequestException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.security.MessageDigest;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component
@RequiredArgsConstructor
public class SlackInterceptor implements HandlerInterceptor {

    @Value("${slack.signing.secret}")
    private String SLACK_SIGNING_SECRET;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);

        String method = request.getMethod();
        String signature = request.getHeader("X-Slack-Signature");
        String timestamp = request.getHeader("X-Slack-Request-Timestamp");
        String payload = getRequestBody(requestWrapper);

        if (!method.equalsIgnoreCase("POST") || !isValidRequest(payload, signature, timestamp)) {
            throw new SlackInvalidRequestException();
        }

        return true;
    }

    private String getRequestBody(ContentCachingRequestWrapper requestWrapper) {
        StringBuilder stringBuilder = new StringBuilder();
        String line;

        try (BufferedReader reader = requestWrapper.getReader()) {
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            throw new FailedToReadSlackRequestException();
        }

        return stringBuilder.toString();
    }

    private boolean isValidRequest(String payload, String signature, String timestamp) {
        try {
            // Slack 검증용 문자열 생성
            String baseString = "v0:" + timestamp + ":" + payload;
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret = new SecretKeySpec(SLACK_SIGNING_SECRET.getBytes(UTF_8), "HmacSHA256");
            mac.init(secret);
            byte[] rawHmac = mac.doFinal(baseString.getBytes(UTF_8));

            // HMAC 결과값을 16진수 문자열로 변환
            StringBuilder sb = new StringBuilder();
            for (byte b : rawHmac) {
                sb.append(String.format("%02x", b));
            }
            String calculatedSignature = "v0=" + sb;  // 'v0='을 포함한 서명 생성

            // 서명 전체를 비교 (constant-time 비교 사용)
            return MessageDigest.isEqual(calculatedSignature.getBytes(UTF_8), signature.getBytes(UTF_8));
        } catch (Exception e) {
            throw new FailedToMatchSlackSignatureException();
        }
    }
}
