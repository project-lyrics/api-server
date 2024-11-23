package com.projectlyrics.server.domain.block.api;

import com.projectlyrics.server.domain.auth.authentication.AuthContext;
import com.projectlyrics.server.domain.auth.authentication.Authenticated;
import com.projectlyrics.server.domain.block.dto.response.BlockResponse;
import com.projectlyrics.server.domain.block.service.BlockCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/blocks")
@RequiredArgsConstructor
public class BlockController {

    private final BlockCommandService blockCommandService;

    @PostMapping
    public ResponseEntity<BlockResponse> create(
            @Authenticated AuthContext authContext,
            @RequestParam(name = "userId") Long blockedId
    ) {
        blockCommandService.create(authContext.getId(), blockedId);

        return ResponseEntity
                .ok(new BlockResponse(true));
    }

    @DeleteMapping
    public ResponseEntity<BlockResponse> delete(
            @Authenticated AuthContext authContext,
            @RequestParam(name = "userId") Long blockedId
    ) {
        blockCommandService.delete(authContext.getId(), blockedId);

        return ResponseEntity
                .ok(new BlockResponse(true));
    }
}
