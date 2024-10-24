package com.projectlyrics.server.domain.favoriteartist.service;

import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.artist.exception.ArtistNotFoundException;
import com.projectlyrics.server.domain.artist.repository.ArtistQueryRepository;
import com.projectlyrics.server.domain.common.entity.enumerate.EntityStatusEnum;
import com.projectlyrics.server.domain.favoriteartist.dto.request.CreateFavoriteArtistListRequest;
import com.projectlyrics.server.domain.favoriteartist.entity.FavoriteArtist;
import com.projectlyrics.server.domain.favoriteartist.entity.FavoriteArtistCreate;
import com.projectlyrics.server.domain.favoriteartist.exception.FavoriteArtistAlreadyExistsException;
import com.projectlyrics.server.domain.favoriteartist.exception.FavoriteArtistNotFoundException;
import com.projectlyrics.server.domain.favoriteartist.repository.FavoriteArtistCommandRepository;
import com.projectlyrics.server.domain.favoriteartist.repository.FavoriteArtistQueryRepository;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.exception.UserNotFoundException;
import com.projectlyrics.server.domain.user.repository.UserQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class FavoriteArtistCommandService {

    private final FavoriteArtistCommandRepository favoriteArtistCommandRepository;
    private final FavoriteArtistQueryRepository favoriteArtistQueryRepository;
    private final UserQueryRepository userQueryRepository;
    private final ArtistQueryRepository artistQueryRepository;

    public synchronized FavoriteArtist create(Long userId, Long artistId) {
        User user = userQueryRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        Artist artist = artistQueryRepository.findById(artistId)
                .orElseThrow(ArtistNotFoundException::new);

        if (favoriteArtistQueryRepository.findByUserIdAndArtistId(userId, artistId).isPresent()) {
            throw new FavoriteArtistAlreadyExistsException();
        }
        user.setStatus(EntityStatusEnum.IN_USE);

        return favoriteArtistCommandRepository.save(FavoriteArtist.create(FavoriteArtistCreate.of(user, artist)));
    }

    public void createAll(Long userId, CreateFavoriteArtistListRequest request) {
        User user = userQueryRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        List<Artist> userFavoriteArtistList = getUserFavoriteArtistList(userId);

        List<Artist> artistList = artistQueryRepository.findAllByIds(request.artistIds());
        List<FavoriteArtist> newFavoriteArtistList = artistList.stream()
                .map(artist -> FavoriteArtist.create(FavoriteArtistCreate.of(user, artist)))
                .toList();

        newFavoriteArtistList.forEach(newFavoriteArtist -> {
            if (!userFavoriteArtistList.contains(newFavoriteArtist.getArtist())) {
                favoriteArtistCommandRepository.save(newFavoriteArtist);
            }
        });

        user.setStatus(EntityStatusEnum.IN_USE);
    }

    public void delete(Long userId, Long artistId) {
        favoriteArtistQueryRepository.findByUserIdAndArtistId(userId, artistId)
                .filter(favoriteArtist -> favoriteArtist.isUser(userId))
                .ifPresentOrElse(
                        favoriteArtist -> favoriteArtist.delete(userId, Clock.systemDefaultZone()),
                        () -> { throw new FavoriteArtistNotFoundException(); }
                );
    }

    private List<Artist> getUserFavoriteArtistList(Long userId) {
        return favoriteArtistQueryRepository.findAllByUserIdFetchArtist(userId)
                .stream()
                .map(FavoriteArtist::getArtist)
                .toList();
    }
}
