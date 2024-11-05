package com.projectlyrics.server.domain.auth.authentication.interceptor;

import com.projectlyrics.server.global.exception.UpdateRequiredException;
import com.projectlyrics.server.global.exception.VersionHeaderNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Profile({"dev", "local"})
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
            if (!request.getHeader("App-Version").equals(version)) {
                throw new UpdateRequiredException();
            }
        } catch (NullPointerException e) {
            throw new VersionHeaderNotFoundException();
        }

        return true;
    }
}
