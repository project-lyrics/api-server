package com.projectlyrics.server.domain.discipline.dto.request;

import com.projectlyrics.server.domain.discipline.domain.DisciplineReason;
import com.projectlyrics.server.domain.discipline.domain.DisciplineType;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record DisciplineCreateRequest (
        @NotNull
        Long userId,
        Long artistId,
        @NotNull
        DisciplineReason disciplineReason,
        @NotNull
        DisciplineType disciplineType,

        @NotNull
        LocalDateTime startTime
){
        public static DisciplineCreateRequest of(Long userId, Long artistId, DisciplineReason disciplineReason, DisciplineType disciplineType, LocalDateTime startTime) {
                return new DisciplineCreateRequest(userId, artistId, disciplineReason, disciplineType, startTime);
        }
}
