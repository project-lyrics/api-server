package com.projectlyrics.server.domain.user;

import static org.assertj.core.api.Assertions.assertThat;

import com.projectlyrics.server.common.IntegrationTest;
import com.projectlyrics.server.common.fixture.UserFixture;
import com.projectlyrics.server.domain.auth.entity.enumerate.AuthProvider;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.repository.UserCommandRepository;
import com.projectlyrics.server.domain.user.repository.UserQueryRepository;
import com.projectlyrics.server.domain.user.service.UserQueryService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class UserQueryServiceTest extends IntegrationTest {

  @Autowired
  UserQueryService sut;

  @Autowired
  UserQueryRepository userQueryRepository;

  @Autowired
  UserCommandRepository userCommandRepository;

  @Test
  void 소셜_정보로_유저를_조회해야_한다() throws Exception {
    //given
    User savedUser = userCommandRepository.save(UserFixture.createKakao());

    //when
    User user = sut.getUserBySocialInfo(savedUser.getAuth().getSocialId(), AuthProvider.KAKAO)
        .orElseThrow(NullPointerException::new);

    //then
    assertThat(user).isEqualTo(savedUser);
  }

  @Test
  void 소셜_정보로_없는_유저를_조회하면_Optional_empty을_반환해야_한다() throws Exception {
    //given

    //when
    Optional<User> user = sut.getUserBySocialInfo("socialId", AuthProvider.KAKAO);

    //then
    assertThat(user).isEmpty();
  }
}
