package com.projectlyrics.server.domain.user.controller;

import com.projectlyrics.server.domain.auth.authentication.AuthContext;
import com.projectlyrics.server.domain.auth.authentication.Authenticated;
import com.projectlyrics.server.domain.user.controller.dto.response.UserProfileResponse;
import com.projectlyrics.server.domain.user.controller.dto.response.UserUpdateResponse;
import com.projectlyrics.server.domain.user.controller.dto.request.UserUpdateRequest;
import com.projectlyrics.server.domain.user.service.UserCommandService;
import com.projectlyrics.server.domain.user.service.UserQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile(
            @Authenticated AuthContext authContext
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userQueryService.getProfile(authContext.getId()));
    }

    @PatchMapping
    public ResponseEntity<UserUpdateResponse> update(
            @Authenticated AuthContext authContext,
            @RequestBody @Valid UserUpdateRequest request
    ) {
        userCommandService.update(request, authContext.getId());

        return ResponseEntity
                .ok(new UserUpdateResponse(true));
    }
}
