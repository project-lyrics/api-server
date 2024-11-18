package com.projectlyrics.server.domain.auth.authentication.interceptor;

import com.projectlyrics.server.global.exception.UpdateRequiredException;
import com.projectlyrics.server.global.exception.VersionHeaderNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Profile({"dev", "prod", })
@Component
public class VersionVerificationInterceptor implements HandlerInterceptor {

    private final String version;

    public VersionVerificationInterceptor(
            @Value("${version}") String version
    ) {
        this.version = version;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            if (compareVersions(request.getHeader("App-Version"), version) < 0) {
                throw new UpdateRequiredException();
            }
        } catch (NullPointerException e) {
            throw new VersionHeaderNotFoundException();
        }

        return true;
    }

    private static int compareVersions(String version1, String version2) {
        String[] v1Parts = version1.split("\\.");
        String[] v2Parts = version2.split("\\.");

        int length = Math.max(v1Parts.length, v2Parts.length);

        for (int i = 0; i < length; i++) {
            int v1Part = i < v1Parts.length ? Integer.parseInt(v1Parts[i]) : 0;
            int v2Part = i < v2Parts.length ? Integer.parseInt(v2Parts[i]) : 0;

            if (v1Part < v2Part) {
                return -1;
            } else if (v1Part > v2Part) {
                return 1;
            }
        }
        return 0;
    }
}
