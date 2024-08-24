package com.projectlyrics.server.domain.auth.api;

import com.projectlyrics.server.domain.auth.dto.response.TokenValidateResponse;
import com.projectlyrics.server.domain.auth.dto.request.TokenReissueRequest;
import com.projectlyrics.server.domain.auth.dto.request.AuthSignUpRequest;
import com.projectlyrics.server.domain.auth.dto.request.AuthSignInRequest;
import com.projectlyrics.server.domain.auth.service.AuthCommandService;
import com.projectlyrics.server.domain.auth.dto.response.AuthTokenResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@RestController
public class AuthController {

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
    public ResponseEntity<AuthTokenResponse> reissueToken(
            @RequestBody @Valid TokenReissueRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(authCommandService.reissueToken(request.refreshToken()));
    }

    @GetMapping("/validate-token")
    public ResponseEntity<TokenValidateResponse> validateToken() {
        return ResponseEntity.ok(new TokenValidateResponse(true));
    }
}
