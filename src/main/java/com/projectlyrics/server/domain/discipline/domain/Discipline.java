package com.projectlyrics.server.domain.discipline.domain;

import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.note.entity.Note;
import com.projectlyrics.server.domain.report.domain.ReportReason;
import com.projectlyrics.server.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "disciplines")
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Discipline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Enumerated(value = EnumType.STRING)
    private ReportReason reason;

    @Enumerated(value = EnumType.STRING)
    private DisciplineType type;

    @ManyToOne(fetch = FetchType.LAZY)
    private Artist artist;

}
