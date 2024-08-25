package com.projectlyrics.server.domain.notification.domain;

import com.google.firebase.messaging.Message;
import com.projectlyrics.server.domain.comment.domain.Comment;
import com.projectlyrics.server.domain.common.entity.BaseEntity;
import com.projectlyrics.server.domain.note.entity.Note;
import com.projectlyrics.server.domain.notification.domain.event.CommentEvent;
import com.projectlyrics.server.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "notifications")
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private NotificationType type;

    @ManyToOne(fetch = FetchType.LAZY)
    private User sender;
    @ManyToOne(fetch = FetchType.LAZY)
    private User receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    private Note note;
    @ManyToOne(fetch = FetchType.LAZY)
    private Comment comment;

    private Notification(
            Long id,
            NotificationType type,
            User sender,
            User receiver,
            Note note,
            Comment comment
    ) {
        this.id = id;
        this.type = type;
        this.sender = sender;
        this.receiver = receiver;
        this.note = note;
        this.comment = comment;
    }

    private Notification(
            NotificationType type,
            User sender,
            User receiver,
            Note note,
            Comment comment
    ) {
        this(null, type, sender, receiver, note, comment);
    }

    public static Notification create(CommentEvent event) {
        return new Notification(
                NotificationType.COMMENT_ON_NOTE,
                event.sender(),
                event.receiver(),
                event.note(),
                event.comment()
        );
    }

    public Message getMessage() {
        Message.Builder builder = Message.builder()
                .setToken(receiver.getFcmToken());

        switch (type) {
            case COMMENT_ON_NOTE:
                return builder
                        .putData("type", type.name())
                        .putData("senderId", sender.getId().toString())
                        .putData("senderNickname", sender.getNickname().getValue())
                        .putData("noteId", note.getId().toString())
                        .putData("noteTitle", note.getContent())
                        .build();
            default:
                throw new IllegalArgumentException("Invalid notification type");
        }
    }
}
