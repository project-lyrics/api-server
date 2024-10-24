package com.projectlyrics.server.domain.discipline.domain;

import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.common.entity.BaseEntity;
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
public class Discipline extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Artist artist;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Enumerated(value = EnumType.STRING)
    private DisciplineReason reason;

    @Enumerated(value = EnumType.STRING)
    private DisciplineType type;

    private String notificationContent;

    private Discipline(Long id, User user, Artist artist, DisciplineReason reason, DisciplineType type, LocalDateTime startTime, String notificationContent) {
        this.id = id;
        this.user = user;
        this.artist = artist;
        this.reason = reason;
        this.type = type;
        this.startTime = startTime
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
        this.endTime = this.startTime.plus(type.getPeriod());
        this.notificationContent = notificationContent
                .replace("{시작시간}", startTime.getYear() + "년 " + startTime.getMonthValue() + "월 " + startTime.getDayOfMonth() + "일 " + startTime.getHour()+ "시")
                .replace("{종료시간}", endTime.getYear() + "년 " + endTime.getMonthValue() + "월 " + endTime.getDayOfMonth() + "일 " + endTime.getHour()+ "시");
    }

    public static Discipline create(DisciplineCreate disciplineCreate) {
        return new Discipline(
                null,
                disciplineCreate.user(),
                disciplineCreate.artist(),
                disciplineCreate.reason(),
                disciplineCreate.type(),
                disciplineCreate.startTime(),
                disciplineCreate.notificationContent()
        );
    }

    public static Discipline createWithId(Long id, DisciplineCreate disciplineCreate) {
        return new Discipline(
                id,
                disciplineCreate.user(),
                disciplineCreate.artist(),
                disciplineCreate.reason(),
                disciplineCreate.type(),
                disciplineCreate.startTime(),
                disciplineCreate.notificationContent()
        );
    }
}
