package com.projectlyrics.server.domain.auth.api;

import static com.projectlyrics.server.domain.auth.api.util.AuthHttpHeaders.ADMIN_SECRET;
import static com.projectlyrics.server.domain.auth.api.util.AuthHttpHeaders.AUTHORIZATION;

import com.projectlyrics.server.domain.auth.dto.request.AuthSignUpRequest;
import com.projectlyrics.server.domain.auth.dto.request.AuthSignInRequest;
import com.projectlyrics.server.domain.auth.dto.response.AuthTokenReissueResponse;
import com.projectlyrics.server.domain.auth.service.AuthCommandService;
import com.projectlyrics.server.domain.auth.dto.response.AuthTokenResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@RestController
public class AuthController implements AuthControllerSwagger {

    private final AuthCommandService authCommandService;

    @PostMapping("/sign-in")
    public ResponseEntity<AuthTokenResponse> signIn(
            @RequestBody @Valid AuthSignInRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(authCommandService.signIn(request));
    }

    @PostMapping("/sign-up")
    public ResponseEntity<AuthTokenResponse> signUp(
            @RequestBody @Valid AuthSignUpRequest request
    ) {
        return ResponseEntity
                .ok(authCommandService.signUp(request));
    }

    @PostMapping("/token")
    public ResponseEntity<AuthTokenReissueResponse> reissueToken(
            @RequestHeader(AUTHORIZATION) String refreshToken
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(authCommandService.reissueAccessToken(refreshToken));
    }

    @PostMapping("/admin")
    public ResponseEntity<AuthTokenResponse> signIn(
            @RequestHeader(ADMIN_SECRET) String adminSecret,
            @RequestBody @Valid AuthSignInRequest loginRequest
    ) {
        authCommandService.validateAdminSecret(adminSecret);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authCommandService.signIn(loginRequest));
    }
}
