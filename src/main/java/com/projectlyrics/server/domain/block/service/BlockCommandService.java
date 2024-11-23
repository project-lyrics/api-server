package com.projectlyrics.server.domain.block.service;

import com.projectlyrics.server.domain.block.domain.Block;
import com.projectlyrics.server.domain.block.exception.BlockAlreadyExistsException;
import com.projectlyrics.server.domain.block.exception.BlockNotFoundException;
import com.projectlyrics.server.domain.block.repository.BlockCommandRepository;
import com.projectlyrics.server.domain.block.repository.BlockQueryRepository;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.exception.UserNotFoundException;
import com.projectlyrics.server.domain.user.repository.UserQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

@Service
@Transactional
@RequiredArgsConstructor
public class BlockCommandService {

    private final BlockCommandRepository blockCommandRepository;
    private final BlockQueryRepository blockQueryRepository;
    private final UserQueryRepository userQueryRepository;

    public synchronized Block create(Long blockerId, Long blockedId) {
        User blocker = userQueryRepository.findById(blockerId)
                .orElseThrow(UserNotFoundException::new);
        User blocked = userQueryRepository.findById(blockedId)
                .orElseThrow(UserNotFoundException::new);

        checkIfBlockExists(blockerId, blockedId);

        return blockCommandRepository.save(new Block(blocker, blocked));
    }

    private void checkIfBlockExists(Long blockerId, Long blockedId) {
        blockQueryRepository.findNotDeletedByBlockerIdAndBlockedId(blockerId, blockedId)
                .ifPresent(block -> { throw new BlockAlreadyExistsException(); });
    }

    public void delete(Long blockerId, Long blockedId) {
        blockQueryRepository.findNotDeletedByBlockerIdAndBlockedId(blockerId, blockedId)
                .ifPresentOrElse(
                        block -> block.delete(blockerId, Clock.systemDefaultZone()),
                        () -> { throw new BlockNotFoundException(); }
                );
    }
}
