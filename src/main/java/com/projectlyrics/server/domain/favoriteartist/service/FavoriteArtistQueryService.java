package com.projectlyrics.server.domain.favoriteartist.service;

import com.projectlyrics.server.domain.common.dto.util.CursorBasePaginatedResponse;
import com.projectlyrics.server.domain.favoriteartist.dto.response.FavoriteArtistResponse;
import com.projectlyrics.server.domain.favoriteartist.repository.FavoriteArtistCommandRepository;
import com.projectlyrics.server.domain.favoriteartist.repository.FavoriteArtistQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FavoriteArtistQueryService {

    private final FavoriteArtistQueryRepository favoriteArtistQueryRepository;

    public CursorBasePaginatedResponse<FavoriteArtistResponse> findFavoriteArtists(Long userId, Long cursorId, PageRequest pageRequest) {
        Slice<FavoriteArtistResponse> favoriteArtistSlice = favoriteArtistQueryRepository.findAllByUserId(
                userId,
                cursorId,
                pageRequest
        ).map(FavoriteArtistResponse::of);
        return CursorBasePaginatedResponse.of(favoriteArtistSlice);
    }

    public List<FavoriteArtistResponse> findAllHavingNotesOfUser(Long userId) {
        return favoriteArtistQueryRepository.findAllHavingNotesOfUser(userId)
                .stream()
                .map(FavoriteArtistResponse::of)
                .toList();
    }

    public List<FavoriteArtistResponse> findAllBookmarked(Long userId) {
        return favoriteArtistQueryRepository.findAllBookmarked(userId)
                .stream()
                .map(FavoriteArtistResponse::of)
                .toList();
    }

    public boolean existsByUserIdAndArtistId(Long userId, Long artistId) {
        return favoriteArtistQueryRepository.findByUserIdAndArtistId(userId, artistId)
                .isPresent();
    }
}
