package com.projectlyrics.server.domain.note.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "lyrics")
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Lyrics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;
    @Enumerated(EnumType.STRING)
    private NoteBackground background;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "lyrics")
    private Note note;

    private Lyrics(
            Long id,
            String content,
            NoteBackground background
    ) {
        this.id = id;
        this.content = content;
        this.background = background;
    }

    private Lyrics(
            String content,
            NoteBackground background
    ) {
        this(null, content, background);
    }

    public static Lyrics of(
            String content,
            NoteBackground background
    ) {
        if (content == null || background == null) {
            return null;
        }

        return new Lyrics(content, background);
    }

    public void setNote(Note note) {
        this.note = note;
    }
}
