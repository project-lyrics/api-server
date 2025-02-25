package com.projectlyrics.server.domain.event.api;

import com.projectlyrics.server.domain.auth.authentication.AuthContext;
import com.projectlyrics.server.domain.auth.authentication.Authenticated;
import com.projectlyrics.server.domain.common.dto.util.CursorBasePaginatedResponse;
import com.projectlyrics.server.domain.event.dto.request.EventCreateRequest;
import com.projectlyrics.server.domain.event.dto.response.EventCreateResponse;
import com.projectlyrics.server.domain.event.dto.response.EventGetResponse;
import com.projectlyrics.server.domain.event.dto.response.EventRefusalResponse;
import com.projectlyrics.server.domain.event.service.EventCommandService;
import com.projectlyrics.server.domain.event.service.EventQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

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
    public ResponseEntity<CursorBasePaginatedResponse<EventGetResponse>> getAllExcludingRefusals(
            @Authenticated AuthContext authContext,
            @RequestParam(name = "cursor", required = false) Long cursor,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        CursorBasePaginatedResponse<EventGetResponse> response = eventQueryService.getAllExcludingRefusals(authContext.getId(), cursor, size);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @PostMapping("/refuse")
    public ResponseEntity<EventRefusalResponse> refuse(
            @Authenticated AuthContext authContext,
            @RequestHeader("Device-Id") String deviceId,
            @RequestParam("eventId") Long eventId
    ) {
        if (authContext.isAnonymous()) {
            eventCommandService.refuseByUser(eventId, authContext.getId());
        } else {
            eventCommandService.refuseByDevice(eventId, deviceId);
        }

        return ResponseEntity
                .ok(new EventRefusalResponse(true));
    }
}
