package com.projectlyrics.server.domain.note.entity;

import com.projectlyrics.server.domain.common.entity.BaseEntity;
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
public class Note extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;
    @Enumerated(EnumType.STRING)
    private NoteStatus noteStatus;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Lyrics lyrics;

    @ManyToOne(fetch = FetchType.LAZY)
    private User publisher;

    @ManyToOne(fetch = FetchType.LAZY)
    private Song song;

    private Note(
            Long id,
            String content,
            Lyrics lyrics,
            NoteStatus noteStatus,
            User publisher,
            Song song
    ) {
        this.id = id;
        this.content = content;
        this.noteStatus = noteStatus;
        this.publisher = publisher;
        this.song = song;
        addLyrics(lyrics);
    }

    private Note(
            String content,
            Lyrics lyrics,
            NoteStatus noteStatus,
            User publisher,
            Song song
    ) {
        this(null, content, lyrics, noteStatus, publisher, song);
    }

    public static Note create(NoteCreate noteCreate) {
        return new Note(
                noteCreate.content(),
                Lyrics.of(noteCreate.lyrics(), noteCreate.background()),
                noteCreate.status(),
                noteCreate.publisher(),
                noteCreate.song()
        );
    }

    private void addLyrics(Lyrics lyrics) {
        if (lyrics != null) {
            this.lyrics = lyrics;
            lyrics.setNote(this);
        }
    }
}
