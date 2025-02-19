package com.projectlyrics.server.domain.notification.domain;

import com.projectlyrics.server.domain.comment.domain.Comment;
import com.projectlyrics.server.domain.common.entity.BaseEntity;
import com.projectlyrics.server.domain.note.entity.Note;
import com.projectlyrics.server.domain.notification.domain.event.CommentEvent;
import com.projectlyrics.server.domain.notification.domain.event.DisciplineEvent;
import com.projectlyrics.server.domain.notification.domain.event.PublicEvent;
import com.projectlyrics.server.domain.notification.exception.NotificationReceiverUnmatchException;
import com.projectlyrics.server.domain.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
    private String content;
    private boolean checked;

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
            String content,
            User sender,
            User receiver,
            Note note,
            Comment comment
    ) {
        this.id = id;
        this.type = type;
        this.sender = sender;
        this.content = content;
        this.receiver = receiver;
        this.note = note;
        this.comment = comment;
        this.checked = false;
    }

    public static Notification create(CommentEvent event) {
        return new Notification(
                null,
                NotificationType.COMMENT_ON_NOTE,
                null,
                event.sender(),
                event.receiver(),
                event.note(),
                event.comment()
        );
    }

    public static Notification create(DisciplineEvent event) {
        return new Notification(
                null,
                NotificationType.DISCIPLINE,
                event.discipline().getNotificationContent(),
                event.sender(),
                event.receiver(),
                null,
                null
        );
    }

    public static Notification create(PublicEvent event) {
        return new Notification(
                null,
                NotificationType.PUBLIC,
                event.content(),
                event.sender(),
                event.receiver(),
                null,
                null
        );
    }
  
    public void check(Long userId) {
        if (!receiver.getId().equals(userId)) {
            throw new NotificationReceiverUnmatchException();

        }

        checked = true;
    }
}
