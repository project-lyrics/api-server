package com.projectlyrics.server.domain.event.api;

import com.projectlyrics.server.domain.event.dto.request.EventCreateRequest;
import com.projectlyrics.server.domain.event.dto.response.EventCreateResponse;
import com.projectlyrics.server.domain.event.service.EventCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
