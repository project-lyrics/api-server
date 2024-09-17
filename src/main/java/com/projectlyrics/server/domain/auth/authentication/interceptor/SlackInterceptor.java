package com.projectlyrics.server.domain.auth.authentication.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
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

        // 요청 본문을 직접 가져오는 방식
        String payload = getRequestBody(requestWrapper);

        if (!method.equalsIgnoreCase("POST") || !isValidSlackRequest(payload, signature, timestamp)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized로 변경
            return false;
        }

        return true;
    }

    // 요청 본문을 읽는 메서드
    private String getRequestBody(ContentCachingRequestWrapper requestWrapper) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        try (BufferedReader reader = requestWrapper.getReader()) {
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        }
        return stringBuilder.toString();
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
            String calculatedSignature = "v0=" + sb.toString();  // 'v0='을 포함한 서명 생성

            // 디버깅용 출력
//            System.out.println("-------------------------------");
//            System.out.println(baseString);
            System.out.println("==============================");
            System.out.println("Calculated Signature: " + calculatedSignature);
            System.out.println("Slack Signature: " + signature);
            System.out.println("==============================");

            // 서명 전체를 비교 (constant-time 비교 사용)
            return MessageDigest.isEqual(calculatedSignature.getBytes(StandardCharsets.UTF_8),
                    signature.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}



