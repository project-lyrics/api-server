package com.projectlyrics.server.domain.notification.api;

import com.projectlyrics.server.domain.auth.authentication.AuthContext;
import com.projectlyrics.server.domain.auth.authentication.Authenticated;
import com.projectlyrics.server.domain.notification.api.dto.request.PublicNotificationCreateRequest;
import com.projectlyrics.server.domain.notification.api.dto.response.PublicNotificationCreateResponse;
import com.projectlyrics.server.domain.notification.service.NotificationCommandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    public final NotificationCommandService notificationCommandService;

    @PostMapping("/public")
    public ResponseEntity<PublicNotificationCreateResponse> createPublicNotification(
            @Authenticated AuthContext authContext,
            @RequestBody @Valid PublicNotificationCreateRequest request
    ) {
        notificationCommandService.createPublicNotification(authContext.getId(), request.content());

        return ResponseEntity.ok(new PublicNotificationCreateResponse(true));
    }
}