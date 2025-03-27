package com.projectlyrics.server.domain.view.domain;


import com.projectlyrics.server.domain.common.entity.BaseEntity;
import com.projectlyrics.server.domain.note.entity.Note;
import com.projectlyrics.server.domain.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "total_views")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TotalView extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "note_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Note note;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private String deviceId;

    private TotalView(
            Note note,
            User user,
            String deviceId
    ) {
        this.note = note;
        this.user = user;
        this.deviceId = deviceId;
    }

    public static TotalView create(TotalViewCreate totalViewCreate) {
        return new TotalView(
                totalViewCreate.note(),
                totalViewCreate.user(),
                totalViewCreate.deviceId()
        );
    }

    public static TotalView createWithId(Long id, TotalViewCreate totalViewCreate) {
        return new TotalView(
                id,
                totalViewCreate.note(),
                totalViewCreate.user(),
                totalViewCreate.deviceId()
        );
    }
}
