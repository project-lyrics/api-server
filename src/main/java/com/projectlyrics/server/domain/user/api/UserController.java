package com.projectlyrics.server.domain.user.api;

import com.projectlyrics.server.domain.auth.authentication.AuthContext;
import com.projectlyrics.server.domain.auth.authentication.Authenticated;
import com.projectlyrics.server.domain.user.dto.request.UserUpdateRequest;
import com.projectlyrics.server.domain.user.dto.response.UserProfileResponse;
import com.projectlyrics.server.domain.user.dto.response.UserUpdateResponse;
import com.projectlyrics.server.domain.user.service.UserCommandService;
import com.projectlyrics.server.domain.user.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserQueryService userQueryService;
    private final UserCommandService userCommandService;

    @GetMapping
    public ResponseEntity<UserProfileResponse> getProfile(
            @Authenticated AuthContext authContext
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userQueryService.getById(authContext.getId()));
    }

    @PatchMapping
    public ResponseEntity<UserUpdateResponse> update(
            @Authenticated AuthContext authContext,
            @RequestBody UserUpdateRequest request
    ) {
        userCommandService.update(request, authContext.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new UserUpdateResponse(true));
    }
}
