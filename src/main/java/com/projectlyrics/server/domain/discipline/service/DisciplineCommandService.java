package com.projectlyrics.server.domain.discipline.service;

import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.artist.exception.ArtistNotFoundException;
import com.projectlyrics.server.domain.artist.repository.ArtistQueryRepository;
import com.projectlyrics.server.domain.auth.service.AuthCommandService;
import com.projectlyrics.server.domain.discipline.domain.Discipline;
import com.projectlyrics.server.domain.discipline.domain.DisciplineCreate;
import com.projectlyrics.server.domain.discipline.domain.DisciplineType;
import com.projectlyrics.server.domain.discipline.dto.request.DisciplineCreateRequest;
import com.projectlyrics.server.domain.discipline.repository.DisciplineCommandRepository;
import com.projectlyrics.server.domain.notification.domain.event.DisciplineEvent;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.exception.UserNotFoundException;
import com.projectlyrics.server.domain.user.repository.UserQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class DisciplineCommandService {

    @Value("${admin}")
    private Long adminUserId;

    private final DisciplineCommandRepository disciplineCommandRepository;
    private final UserQueryRepository userQueryRepository;
    private final ArtistQueryRepository artistQueryRepository;
    private final AuthCommandService authCommandService;
    private final ApplicationEventPublisher eventPublisher;

    public Discipline create(DisciplineCreateRequest request) {

        User user = userQueryRepository.findById(request.userId())
                .orElseThrow(UserNotFoundException::new);
        Artist artist = artistQueryRepository.findById(request.artistId())
                .orElseThrow(ArtistNotFoundException::new);

        Discipline discipline = disciplineCommandRepository.save(Discipline.create(DisciplineCreate.of(user, artist, request.disciplineReason(), request.disciplineType(), request.startTime(), request.notificationContent())));

        User admin = userQueryRepository.findById(adminUserId).orElseThrow(UserNotFoundException::new);
        eventPublisher.publishEvent(DisciplineEvent.from(admin, discipline));

        if (discipline.getType() == DisciplineType.FORCED_WITHDRAWAL) {
            authCommandService.forcedWithdrawal(user);
        }

        return discipline;
    }
}
