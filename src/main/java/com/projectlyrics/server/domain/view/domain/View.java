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
@Table(name = "views")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class View extends BaseEntity {

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

    private View(
            Note note,
            User user,
            String deviceId
    ) {
        this.note = note;
        this.user = user;
        this.deviceId = deviceId;
    }

    public static View create(ViewCreate viewCreate) {
        return new View(
                viewCreate.note(),
                viewCreate.user(),
                viewCreate.deviceId()
        );
    }

    public static View createWithId(Long id, ViewCreate viewCreate) {
        return new View(
                id,
                viewCreate.note(),
                viewCreate.user(),
                viewCreate.deviceId()
        );
    }
}
