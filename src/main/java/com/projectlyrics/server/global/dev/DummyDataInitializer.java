package com.projectlyrics.server.global.dev;

import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.artist.repository.ArtistCommandRepository;
import com.projectlyrics.server.domain.auth.entity.Auth;
import com.projectlyrics.server.domain.auth.entity.enumerate.AuthProvider;
import com.projectlyrics.server.domain.auth.entity.enumerate.Role;
import com.projectlyrics.server.domain.auth.jwt.JwtTokenProvider;
import com.projectlyrics.server.domain.auth.jwt.dto.AuthToken;
import com.projectlyrics.server.domain.user.entity.Gender;
import com.projectlyrics.server.domain.user.entity.TermsAgreements;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.repository.UserCommandRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Profile({"dev", "local"})
@Component
@RequiredArgsConstructor
public class DummyDataInitializer {

    private final UserCommandRepository userCommandRepository;
    private final ArtistCommandRepository artistCommandRepository;
    private final JwtTokenProvider tokenProvider;

    @PostConstruct
    public void init() {
        artistCommandRepository.save(Artist.of("너드 커넥션", null));
        artistCommandRepository.save(Artist.of("한로로", null));
        artistCommandRepository.save(Artist.of("신지훈", null));
        artistCommandRepository.save(Artist.of("하현상", null));
        artistCommandRepository.save(Artist.of("잔나비", null));
        artistCommandRepository.save(Artist.of("아이유", null));
        artistCommandRepository.save(Artist.of("ed sheeran", null));
        artistCommandRepository.save(Artist.of("10cm", null));
        artistCommandRepository.save(Artist.of("oasis", null));
        artistCommandRepository.save(Artist.of("국카스텐", null));
        artistCommandRepository.save(Artist.of("artist1", null));
        artistCommandRepository.save(Artist.of("artist2", null));
        artistCommandRepository.save(Artist.of("artist3", null));
        artistCommandRepository.save(Artist.of("artist4", null));
        artistCommandRepository.save(Artist.of("artist5", null));
        User user = userCommandRepository.save(User.of(Auth.of(AuthProvider.KAKAO, Role.USER, "socialId"), "test1", Gender.MALE, 1999, List.of(new TermsAgreements(true, "약관1", "약관 내용"))));
        AuthToken authToken = tokenProvider.issueTokens(user.getId());
        log.info("accessToken: {}", authToken.accessToken());
        log.info("refreshToken: {}", authToken.refreshToken());
    }
}
