package com.projectlyrics.server.domain.block.service;

import com.projectlyrics.server.domain.block.exception.BlockAlreadyExistsException;
import com.projectlyrics.server.domain.block.exception.BlockNotFoundException;
import com.projectlyrics.server.domain.block.repository.BlockQueryRepository;
import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.repository.UserCommandRepository;
import com.projectlyrics.server.domain.user.repository.UserQueryRepository;
import com.projectlyrics.server.support.IntegrationTest;
import com.projectlyrics.server.support.fixture.UserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BlockCommandServiceTest extends IntegrationTest {

    @Autowired
    UserCommandRepository userCommandRepository;

    @Autowired
    UserQueryRepository userQueryRepository;

    @Autowired
    BlockQueryRepository blockQueryRepository;

    @Autowired
    BlockCommandService sut;

    private User user;

    @BeforeEach
    void setUp() {
        user = userCommandRepository.save(UserFixture.create());
    }

    @Test
    void 차단을_생성할_수_있다() {
        // given
        int blockedSize = 10;
        for (int i = 0; i < blockedSize; i++) {
            User blocked = userCommandRepository.save(UserFixture.create());
            sut.create(user.getId(), blocked.getId());
        }

        // when
        List<User> result = userQueryRepository.findAllBlocked(user.getId());

        // then
        assertThat(result.size()).isEqualTo(blockedSize);
    }

    @Test
    void 동일한_사용자를_중복으로_차단할_수_없다() {
        // given
        User blocked = userCommandRepository.save(UserFixture.create());
        sut.create(user.getId(), blocked.getId());

        // when, then
        assertThatThrownBy(() -> sut.create(user.getId(), blocked.getId()))
                .isInstanceOf(BlockAlreadyExistsException.class)
                .hasMessage(ErrorCode.BLOCK_ALREADY_EXISTS.getErrorMessage());
    }

    @Test
    void 차단을_해제할_수_있다() {
        // given
        User blocked = userCommandRepository.save(UserFixture.create());
        sut.create(user.getId(), blocked.getId());

        // when
        sut.delete(user.getId(), blocked.getId());

        // then
        assertThat(blockQueryRepository.findNotDeletedByBlockerIdAndBlockedId(user.getId(), blocked.getId()))
                .isEmpty();
    }

    @Test
    void 존재하지_않는_차단을_해제하려_할_때_예외가_발생한다() {
        // when, then
        assertThatThrownBy(() -> sut.delete(user.getId(), 99L))
                .isInstanceOf(BlockNotFoundException.class)
                .hasMessage(ErrorCode.BLOCK_NOT_FOUND.getErrorMessage());
    }
}