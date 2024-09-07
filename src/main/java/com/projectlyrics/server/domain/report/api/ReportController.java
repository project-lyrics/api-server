package com.projectlyrics.server.domain.report.api;

import com.projectlyrics.server.domain.auth.authentication.AuthContext;
import com.projectlyrics.server.domain.auth.authentication.Authenticated;
import com.projectlyrics.server.domain.report.dto.request.ReportCreateRequest;
import com.projectlyrics.server.domain.report.dto.request.ReportResolveRequest;
import com.projectlyrics.server.domain.report.dto.response.ReportCreateResponse;
import com.projectlyrics.server.domain.report.dto.response.ReportResolveResponse;
import com.projectlyrics.server.domain.report.service.ReportCommandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportCommandService reportCommandService;

    @PostMapping
    public ResponseEntity<ReportCreateResponse> create(
            @Authenticated AuthContext authContext,
            @RequestBody @Valid ReportCreateRequest request
    ) {
        reportCommandService.create(request, authContext.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ReportCreateResponse(true));
    }

    @PatchMapping("/{reportId}")
    public ResponseEntity<ReportResolveResponse> resolve(
            @Authenticated AuthContext authContext,
            @PathVariable(name = "reportId") Long reportId,
            @RequestBody @Valid ReportResolveRequest request
    ) {
        reportCommandService.resolve(request, reportId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ReportResolveResponse(true));
    }
}
