package com.projectlyrics.server.domain.comment.domain;

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
@Table(name = "comments")
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    private User writer;

    @ManyToOne(fetch = FetchType.LAZY)
    private Note note;

    private Comment(
            Long id,
            String content,
            User writer,
            Note note
    ) {
        this.id = id;
        this.content = content;
        this.writer = writer;
        this.note = note;
    }

    private Comment(
            String content,
            User writer,
            Note note
    ) {
        this(null, content, writer, note);
    }

    public static Comment create(CommentCreate commentCreate) {
        return new Comment(
                commentCreate.content(),
                commentCreate.writer(),
                commentCreate.note()
        );
    }

    public static Comment createWithId(Long id, CommentCreate commentCreate) {
        return new Comment(
                id,
                commentCreate.content(),
                commentCreate.writer(),
                commentCreate.note()
        );
    }

    public boolean isWriter(Long writerId) {
        return this.writer.getId().equals(writerId);
    }

    public Comment update(CommentUpdate commentUpdate) {
        return new Comment(
                this.id,
                commentUpdate.content(),
                this.writer,
                this.note
        );
    }
}
