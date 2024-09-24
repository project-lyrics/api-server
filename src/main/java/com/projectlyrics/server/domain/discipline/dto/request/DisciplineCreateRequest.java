package com.projectlyrics.server.domain.discipline.dto.request;

import com.projectlyrics.server.domain.discipline.domain.DisciplineReason;
import com.projectlyrics.server.domain.discipline.domain.DisciplineType;
import jakarta.validation.constraints.NotNull;

public record DisciplineCreateRequest (
        @NotNull
        Long userId,
        Long artistId,
        @NotNull
        DisciplineReason disciplineReason,
        @NotNull
        DisciplineType disciplineType
){
        public static DisciplineCreateRequest of(Long userId, Long artistId, DisciplineReason disciplineReason, DisciplineType disciplineType) {
                return new DisciplineCreateRequest(userId, artistId, disciplineReason, disciplineType);
        }
}
