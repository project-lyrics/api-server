package com.projectlyrics.server.domain.view.domain;

import com.projectlyrics.server.domain.common.entity.BaseEntity;
import com.projectlyrics.server.domain.note.entity.Note;
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
@Table(name = "unique_views")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UniqueView extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "note_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Note note;

    private String deviceId;

    private UniqueView(
            Note note,
            String deviceId
    ) {
        this.note = note;
        this.deviceId = deviceId;
    }

    public static UniqueView create(UniqueViewCreate uniqueViewCreate) {
        return new UniqueView(
                uniqueViewCreate.note(),
                uniqueViewCreate.deviceId()
        );
    }

    public static UniqueView createWithId(Long id, UniqueViewCreate uniqueViewCreate) {
        return new UniqueView(
                id,
                uniqueViewCreate.note(),
                uniqueViewCreate.deviceId()
        );
    }
}
