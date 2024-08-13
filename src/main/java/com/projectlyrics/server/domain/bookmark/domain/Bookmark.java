package com.projectlyrics.server.domain.bookmark.domain;

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
@Table(name = "bookmarks")
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bookmark extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Note note;

    private Bookmark(
            Long id,
            User user,
            Note note
    ) {
        this.id = id;
        this.user = user;
        this.note = note;
    }

    private Bookmark(
            User user,
            Note note
    ) {
        this(null, user, note);
    }

    public static Bookmark create(BookmarkCreate bookmarkCreate) {
        return new Bookmark(
                bookmarkCreate.user(),
                bookmarkCreate.note()
        );
    }

    public static Bookmark createWithId(Long id, BookmarkCreate bookmarkCreate) {
        return new Bookmark(
                id,
                bookmarkCreate.user(),
                bookmarkCreate.note()
        );
    }

    public boolean isUser(Long userId) {
        return user.getId().equals(userId);
    }
}
