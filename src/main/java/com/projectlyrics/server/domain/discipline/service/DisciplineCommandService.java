package com.projectlyrics.server.domain.discipline.service;

import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.artist.exception.ArtistNotFoundException;
import com.projectlyrics.server.domain.artist.repository.ArtistQueryRepository;
import com.projectlyrics.server.domain.discipline.domain.Discipline;
import com.projectlyrics.server.domain.discipline.domain.DisciplineCreate;
import com.projectlyrics.server.domain.discipline.dto.request.DisciplineCreateRequest;
import com.projectlyrics.server.domain.discipline.repository.DisciplineCommandRepository;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.exception.UserNotFoundException;
import com.projectlyrics.server.domain.user.repository.UserQueryRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class DisciplineCommandService {

    private final DisciplineCommandRepository disciplineCommandRepository;
    private final UserQueryRepository userQueryRepository;
    private final ArtistQueryRepository artistQueryRepository;

    public Discipline create(DisciplineCreateRequest request) {

        User user = userQueryRepository.findById(request.userId())
                .orElseThrow(UserNotFoundException::new);
        Artist artist = Optional.ofNullable(request.artistId())
                .map(artistId -> artistQueryRepository.findById(artistId)
                        .orElseThrow(ArtistNotFoundException::new))
                .orElse(null);

        Discipline discipline = disciplineCommandRepository.save(Discipline.create(DisciplineCreate.of(user, artist, request.reportReason(), request.disciplineType())));

        return discipline;
    }
}
