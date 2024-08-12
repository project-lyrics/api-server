package com.projectlyrics.server.domain.like.domain;

import com.projectlyrics.server.domain.common.entity.BaseEntity;
import com.projectlyrics.server.domain.note.entity.Note;
import com.projectlyrics.server.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "likes")
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Like extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Note note;

    private Like(
            Long id,
            User user,
            Note note
    ) {
        this.id = id;
        this.user = user;
        this.note = note;
    }

    private Like(
            User user,
            Note note
    ) {
        this(null, user, note);
    }

    public static Like create(LikeCreate likeCreate) {
        return new Like(
                likeCreate.user(),
                likeCreate.note()
        );
    }

    public static Like createWithId(Long id, LikeCreate likeCreate) {
        return new Like(
                id,
                likeCreate.user(),
                likeCreate.note()
        );
    }

    public boolean isUser(Long userId) {
        return this.user.getId().equals(userId);
    }
}
