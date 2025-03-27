package com.projectlyrics.server.domain.view.api;

import com.projectlyrics.server.domain.auth.authentication.AuthContext;
import com.projectlyrics.server.domain.auth.authentication.Authenticated;
import com.projectlyrics.server.domain.view.dto.request.ViewCreateRequest;
import com.projectlyrics.server.domain.view.dto.response.ViewCreateResponse;
import com.projectlyrics.server.domain.view.service.ViewCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/views")
@RequiredArgsConstructor
public class ViewController {

    private final ViewCommandService viewCommandService;

    @PostMapping
    public ResponseEntity<ViewCreateResponse> create(
            @Authenticated AuthContext authContext,
            @RequestHeader("Device-Id") String deviceId,
            @RequestBody ViewCreateRequest request
            ) {
        if (authContext.isAnonymous()) {
            viewCommandService.create(request, null, deviceId);
        } else {
            viewCommandService.create(request, authContext.getId(), deviceId);
        }

        return ResponseEntity
                .ok(new ViewCreateResponse(true));
    }
}
