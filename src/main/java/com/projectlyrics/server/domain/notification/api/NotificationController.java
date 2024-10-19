package com.projectlyrics.server.domain.notification.api;

import com.projectlyrics.server.domain.auth.authentication.AuthContext;
import com.projectlyrics.server.domain.auth.authentication.Authenticated;
import com.projectlyrics.server.domain.common.dto.util.CursorBasePaginatedResponse;
import com.projectlyrics.server.domain.notification.api.dto.request.PublicNotificationCreateRequest;
import com.projectlyrics.server.domain.notification.api.dto.response.NotificationCheckResponse;
import com.projectlyrics.server.domain.notification.api.dto.response.NotificationGetResponse;
import com.projectlyrics.server.domain.notification.api.dto.response.NotificationHasUncheckedResponse;
import com.projectlyrics.server.domain.notification.api.dto.response.PublicNotificationCreateResponse;
import com.projectlyrics.server.domain.notification.service.NotificationCommandService;
import com.projectlyrics.server.domain.notification.service.NotificationQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.projectlyrics.server.domain.notification.domain.NotificationType.COMMENT_ON_NOTE;
import static com.projectlyrics.server.domain.notification.domain.NotificationType.PUBLIC;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    public final NotificationCommandService notificationCommandService;
    public final NotificationQueryService notificationQueryService;

    @PostMapping("/public")
    public ResponseEntity<PublicNotificationCreateResponse> createPublicNotification(
            @Authenticated AuthContext authContext,
            @RequestBody @Valid PublicNotificationCreateRequest request
    ) {
        notificationCommandService.createPublicNotification(authContext.getId(), request.content());

        return ResponseEntity.ok(new PublicNotificationCreateResponse(true));
    }

    @PatchMapping("/{notificationId}")
    public ResponseEntity<NotificationCheckResponse> check(
            @Authenticated AuthContext authContext,
            @PathVariable Long notificationId
    ) {
        notificationCommandService.check(notificationId, authContext.getId());

        return ResponseEntity.ok(new NotificationCheckResponse(true));
    }

    @GetMapping("/public")
    public ResponseEntity<CursorBasePaginatedResponse<NotificationGetResponse>> getAllPublic(
            @Authenticated AuthContext authContext,
            @RequestParam(name = "cursor", required = false) Long cursor,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(notificationQueryService.getRecentNotifications(PUBLIC, authContext.getId(), cursor, size));
    }

    @GetMapping("/personal")
    public ResponseEntity<CursorBasePaginatedResponse<NotificationGetResponse>> getAllPersonal(
            @Authenticated AuthContext authContext,
            @RequestParam(name = "cursor", required = false) Long cursor,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(notificationQueryService.getRecentNotifications(COMMENT_ON_NOTE, authContext.getId(), cursor, size));
    }

    @GetMapping("/check")
    public ResponseEntity<NotificationHasUncheckedResponse> hasUnchecked(
            @Authenticated AuthContext authContext
    ) {
        boolean result = notificationQueryService.hasUnchecked(authContext.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new NotificationHasUncheckedResponse(result));
    }
}
