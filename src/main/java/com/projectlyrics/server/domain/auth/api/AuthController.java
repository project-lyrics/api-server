package com.projectlyrics.server.domain.auth.api;

import com.projectlyrics.server.domain.auth.authentication.AuthContext;
import com.projectlyrics.server.domain.auth.authentication.Authenticated;
import com.projectlyrics.server.domain.auth.dto.response.AuthDeleteResponse;
import com.projectlyrics.server.domain.auth.dto.response.AuthSignOutResponse;
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
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@RestController
public class AuthController {

    private final AuthCommandService authCommandService;

    @PostMapping("/sign-in")
    public ResponseEntity<AuthTokenResponse> signIn(
            @RequestBody @Valid AuthSignInRequest request,
            @RequestHeader("Device-Id") String deviceId
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(authCommandService.signIn(request, deviceId));
    }

    @PostMapping("/sign-up")
    public ResponseEntity<AuthTokenResponse> signUp(
            @RequestBody @Valid AuthSignUpRequest request,
            @RequestHeader("Device-Id") String deviceId
    ) {
        return ResponseEntity
                .ok(authCommandService.signUp(request, deviceId));
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

    @DeleteMapping("/sign-out")
    public ResponseEntity<AuthSignOutResponse> signOut(
            @Authenticated AuthContext authContext
    ) {
        authCommandService.signOut(authContext.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new AuthSignOutResponse(true));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<AuthDeleteResponse> delete(
            @Authenticated AuthContext authContext
    ) {
        authCommandService.delete(authContext.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new AuthDeleteResponse(true));
    }
}
