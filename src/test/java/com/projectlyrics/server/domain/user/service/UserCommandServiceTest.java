package com.projectlyrics.server.domain.user.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.projectlyrics.server.common.IntegrationTest;
import com.projectlyrics.server.common.fixture.UserFixture;
import com.projectlyrics.server.domain.user.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class UserCommandServiceTest extends IntegrationTest {

    @Autowired
    UserCommandService sut;

    @Autowired
    UserQueryService userQueryService;

    @Test
    void 유저_엔티티를_등록해야_한다() throws Exception {
        //given
        User savedUser = sut.create(UserFixture.create());

        //when
        User user = userQueryService.getUserById(savedUser.getId());

        //then
        assertThat(user).isEqualTo(savedUser);
    }
}