package com.projectlyrics.server.domain.discipline.dto.request;

import com.projectlyrics.server.domain.discipline.domain.DisciplineType;
import com.projectlyrics.server.domain.report.domain.ReportReason;
import jakarta.validation.constraints.NotNull;

public record DisciplineCreateRequest (
        @NotNull
        Long userId,
        Long artistId,
        @NotNull
        ReportReason reportReason,
        @NotNull
        DisciplineType disciplineType
){
}
