package com.projectlyrics.server.domain.favoriteartist.service;

import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.artist.service.ArtistQueryService;
import com.projectlyrics.server.domain.favoriteartist.dto.request.CreateFavoriteArtistListRequest;
import com.projectlyrics.server.domain.favoriteartist.entity.FavoriteArtist;
import com.projectlyrics.server.domain.favoriteartist.repository.FavoriteArtistCommandRepository;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class FavoriteArtistCommandService {

    private final FavoriteArtistCommandRepository favoriteArtistCommandRepository;
    private final UserQueryService userQueryService;
    private final ArtistQueryService artistQueryService;

    public void saveAll(Long userId, CreateFavoriteArtistListRequest request) {
        User user = userQueryService.getUserById(userId);
        List<Artist> artistList = artistQueryService.getArtistsByIds(request.artistIds());
        List<FavoriteArtist> favoriteArtistList = artistList.stream()
                .map(artist -> FavoriteArtist.of(user, artist))
                .toList();
        favoriteArtistCommandRepository.saveAll(favoriteArtistList);
    }
}
