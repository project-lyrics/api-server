package com.projectlyrics.server.domain.event.api;

import com.projectlyrics.server.domain.auth.authentication.AuthContext;
import com.projectlyrics.server.domain.auth.authentication.Authenticated;
import com.projectlyrics.server.domain.event.dto.request.EventCreateRequest;
import com.projectlyrics.server.domain.event.dto.response.EventCreateResponse;
import com.projectlyrics.server.domain.event.dto.response.EventRefusalResponse;
import com.projectlyrics.server.domain.event.service.EventCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventCommandService eventCommandService;

    @PostMapping
    public ResponseEntity<EventCreateResponse> create(
            @RequestBody EventCreateRequest request
    ) {
        eventCommandService.create(request);

        return ResponseEntity
                .ok(new EventCreateResponse(true));
    }

    @PostMapping("/refuse")
    public ResponseEntity<EventRefusalResponse> refuse(
            @Authenticated AuthContext authContext,
            @RequestParam("eventId") Long eventId
    ) {
        eventCommandService.refuse(eventId, authContext.getId());

        return ResponseEntity
                .ok(new EventRefusalResponse(true));
    }
}
