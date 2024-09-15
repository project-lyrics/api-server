package com.projectlyrics.server.domain.auth.authentication.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;

@Component
@RequiredArgsConstructor
public class SlackInterceptor implements HandlerInterceptor {

    @Value("${slack.signing.secret}")
    private String SLACK_SIGNING_SECRET;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 요청 본문을 캐싱할 수 있도록 ContentCachingRequestWrapper로 감쌈
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);

        String method = request.getMethod();
        String signature = request.getHeader("X-Slack-Signature");
        String timestamp = request.getHeader("X-Slack-Request-Timestamp");

        // 캐싱된 요청 본문을 가져옴
        String payload = new String(requestWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);

        if (!method.equalsIgnoreCase("POST") || !isValidSlackRequest(payload, signature, timestamp)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized로 변경
            return false;
        }

        return true;
    }

    // Slack 요청이 유효한지 확인하는 메서드
    private boolean isValidSlackRequest(String payload, String signature, String timestamp) {
        try {
            // Slack 검증용 문자열 생성
            String baseString = "v0:" + timestamp + ":" + payload;
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret = new SecretKeySpec(SLACK_SIGNING_SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secret);
            byte[] rawHmac = mac.doFinal(baseString.getBytes(StandardCharsets.UTF_8));

            // HMAC 결과값을 16진수 문자열로 변환
            StringBuilder sb = new StringBuilder();
            for (byte b : rawHmac) {
                sb.append(String.format("%02x", b));
            }
            String calculatedSignature = "v0=" + sb.toString();

            // 서명을 비교
            return MessageDigest.isEqual(calculatedSignature.getBytes(), signature.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}



