package com.projectlyrics.server.domain.discipline.service;

import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.artist.exception.ArtistNotFoundException;
import com.projectlyrics.server.domain.artist.repository.ArtistQueryRepository;
import com.projectlyrics.server.domain.auth.repository.AuthRepository;
import com.projectlyrics.server.domain.bookmark.repository.BookmarkCommandRepository;
import com.projectlyrics.server.domain.comment.repository.CommentCommandRepository;
import com.projectlyrics.server.domain.discipline.domain.Discipline;
import com.projectlyrics.server.domain.discipline.domain.DisciplineCreate;
import com.projectlyrics.server.domain.discipline.domain.DisciplineType;
import com.projectlyrics.server.domain.discipline.dto.request.DisciplineCreateRequest;
import com.projectlyrics.server.domain.discipline.repository.DisciplineCommandRepository;
import com.projectlyrics.server.domain.favoriteartist.repository.FavoriteArtistCommandRepository;
import com.projectlyrics.server.domain.like.repository.LikeCommandRepository;
import com.projectlyrics.server.domain.note.repository.NoteCommandRepository;
import com.projectlyrics.server.domain.notification.domain.event.DisciplineEvent;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.exception.UserNotFoundException;
import com.projectlyrics.server.domain.user.repository.UserQueryRepository;
import java.time.Clock;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class DisciplineCommandService {

    private final DisciplineCommandRepository disciplineCommandRepository;
    private final UserQueryRepository userQueryRepository;
    private final ArtistQueryRepository artistQueryRepository;
    private final AuthRepository authRepository;
    private final BookmarkCommandRepository bookmarkCommandRepository;
    private final CommentCommandRepository commentCommandRepository;
    private final FavoriteArtistCommandRepository favoriteArtistCommandRepository;
    private final LikeCommandRepository likeCommandRepository;
    private final NoteCommandRepository noteCommandRepository;
    private final ApplicationEventPublisher eventPublisher;

    public Discipline create(DisciplineCreateRequest request) {

        User user = userQueryRepository.findById(request.userId())
                .orElseThrow(UserNotFoundException::new);
        Artist artist = Optional.ofNullable(request.artistId())
                .map(artistId -> artistQueryRepository.findById(artistId)
                        .orElseThrow(ArtistNotFoundException::new))
                .orElse(null);

        Discipline discipline = disciplineCommandRepository.save(Discipline.create(DisciplineCreate.of(user, artist, request.disciplineReason(), request.disciplineType(), request.startTime())));
        eventPublisher.publishEvent(DisciplineEvent.from(discipline));

        if (discipline.getType() == DisciplineType.FORCED_WITHDRAWAL) {
            authRepository.findById(user.getSocialInfo().getSocialId())
                    .ifPresent(authRepository::delete);
            bookmarkCommandRepository.deleteAllByUserId(user.getId());
            commentCommandRepository.deleteAllByWriterId(user.getId());
            favoriteArtistCommandRepository.deleteAllByUserId(user.getId());
            likeCommandRepository.deleteAllByUserId(user.getId());
            noteCommandRepository.deleteAllByPublisherId(user.getId());
            user.forcedWithdrawal(Clock.systemDefaultZone());
        }

        return discipline;
    }
}
