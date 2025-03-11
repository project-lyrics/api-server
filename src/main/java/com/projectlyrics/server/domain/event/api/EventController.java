package com.projectlyrics.server.domain.event.api;

import com.projectlyrics.server.domain.auth.authentication.AuthContext;
import com.projectlyrics.server.domain.auth.authentication.Authenticated;
import com.projectlyrics.server.domain.common.dto.util.CursorBasePaginatedResponse;
import com.projectlyrics.server.domain.event.dto.request.EventCreateRequest;
import com.projectlyrics.server.domain.event.dto.response.EventCreateResponse;
import com.projectlyrics.server.domain.event.dto.response.EventCursorBasePaginatedResponse;
import com.projectlyrics.server.domain.event.dto.response.EventGetResponse;
import com.projectlyrics.server.domain.event.dto.response.EventRefusalResponse;
import com.projectlyrics.server.domain.event.service.EventCommandService;
import com.projectlyrics.server.domain.event.service.EventQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    @Value("${event.refusal_period:1}")
    private int refusalPeriod;
    private final EventCommandService eventCommandService;
    private final EventQueryService eventQueryService;

    @PostMapping
    public ResponseEntity<EventCreateResponse> create(
            @RequestBody EventCreateRequest request
    ) {
        eventCommandService.create(request);

        return ResponseEntity
                .ok(new EventCreateResponse(true));
    }

    @GetMapping
    public ResponseEntity<EventCursorBasePaginatedResponse> getAllExceptRefused(
            @Authenticated AuthContext authContext,
            @RequestHeader("Device-Id") String deviceId,
            @RequestParam(name = "cursor", required = false) Long cursor,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        CursorBasePaginatedResponse<EventGetResponse> response;

        if (authContext.isAnonymous()) {
            response = eventQueryService.getAllExceptRefusedByDeviceId(deviceId, cursor, size);
        } else {
            response = eventQueryService.getAllExceptRefusedByUser(authContext.getId(), cursor, size);
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new EventCursorBasePaginatedResponse(refusalPeriod, response));
    }

    @PostMapping("/refuse")
    public ResponseEntity<EventRefusalResponse> refuse(
            @Authenticated AuthContext authContext,
            @RequestHeader("Device-Id") String deviceId,
            @RequestParam("eventId") Long eventId
    ) {
        if (authContext.isAnonymous()) {
            eventCommandService.refuseByDeviceId(eventId, deviceId, refusalPeriod);
        } else {
            eventCommandService.refuseByUser(eventId, authContext.getId(), refusalPeriod);
        }

        return ResponseEntity
                .ok(new EventRefusalResponse(true));
    }
}
