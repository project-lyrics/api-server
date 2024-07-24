package com.projectlyrics.server.domain.note.entity;

import com.projectlyrics.server.domain.song.entity.Song;
import com.projectlyrics.server.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "notes")
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;
    private String lyrics;
    @Enumerated(EnumType.STRING)
    private NoteBackground background;
    @Enumerated(EnumType.STRING)
    private NoteStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    private User publisher;

    @ManyToOne(fetch = FetchType.LAZY)
    private Song song;

    private Note(
            Long id,
            String content,
            String lyrics,
            NoteBackground background,
            NoteStatus status,
            User publisher,
            Song song
    ) {
        this.id = id;
        this.content = content;
        this.lyrics = lyrics;
        this.background = background;
        this.status = status;
        this.publisher = publisher;
        this.song = song;
    }

    private Note(
            String content,
            String lyrics,
            NoteBackground background,
            NoteStatus status,
            User publisher,
            Song song
    ) {
        this(null, content, lyrics, background, status, publisher, song);
    }

    public static Note create(NoteCreate noteCreate) {
        return new Note(
                noteCreate.content(),
                noteCreate.lyrics(),
                noteCreate.background(),
                noteCreate.status(),
                noteCreate.publisher(),
                noteCreate.song()
        );
    }
}
