package com.projectlyrics.server.domain.auth.authentication;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
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
        // ContentCachingRequestWrapper로 래핑
        ContentCachingRequestWrapper requestWrapper = (ContentCachingRequestWrapper) request;

        String method = request.getMethod();
        String signature = request.getHeader("X-Slack-Signature");
        String timestamp = request.getHeader("X-Slack-Request-Timestamp");
        String payload = new String(requestWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);

        if (!method.equalsIgnoreCase("POST") || !isValidSlackRequest(payload, signature, timestamp)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 잘못된 요청
            return false;
        }

        return true;
    }

    private boolean isValidSlackRequest(String payload, String signature, String timestamp) {
        try {
            String baseString = "v0:" + timestamp + ":" + payload;
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret = new SecretKeySpec(SLACK_SIGNING_SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secret);
            byte[] rawHmac = mac.doFinal(baseString.getBytes(StandardCharsets.UTF_8));
            String calculatedSignature = "v0=" + Base64.getEncoder().encodeToString(rawHmac);

            return calculatedSignature.equals(signature);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}


