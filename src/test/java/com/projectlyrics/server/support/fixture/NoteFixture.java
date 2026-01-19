package com.projectlyrics.server.support.fixture;

import com.projectlyrics.server.domain.note.dto.request.NoteCreateRequest;
import com.projectlyrics.server.domain.note.entity.*;
import com.projectlyrics.server.domain.song.entity.Song;
import com.projectlyrics.server.domain.user.entity.User;

public class NoteFixture extends BaseFixture {

    private Long id;
    private String content = "노트 내용";
    private NoteStatus status = NoteStatus.PUBLISHED;
    private NoteType noteType = NoteType.FREE;
    private Lyrics lyrics = Lyrics.of("가사", NoteBackground.DEFAULT);
    private User publisher;
    private Song song;

    public static Note create(User publisher, Song song) {
        return Note.createWithId(
                getUniqueId(),
                NoteCreate.from(
                        new NoteCreateRequest("노트 내용", "가사", NoteBackground.DEFAULT, NoteStatus.PUBLISHED, NoteType.FREE, song.getId()),
                        publisher,
                        song
                )
        );
    }

    private NoteFixture() {
    }

    public static NoteFixture builder() {
        return new NoteFixture();
    }

    public NoteFixture id(Long id) {
        this.id = id;
        return this;
    }

    public NoteFixture content(String content) {
        this.content = content;
        return this;
    }

    public NoteFixture status(NoteStatus status) {
        this.status = status;
        return this;
    }

    public NoteFixture noteType(NoteType noteType) {
        this.noteType = noteType;
        return this;
    }

    public NoteFixture lyrics(Lyrics lyrics) {
        this.lyrics = lyrics;
        return this;
    }

    public NoteFixture publisher(User publisher) {
        this.publisher = publisher;
        return this;
    }

    public NoteFixture song(Song song) {
        this.song = song;
        return this;
    }

    public Note build() {
        return Note.createWithId(
                id,
                NoteCreate.from(
                        new NoteCreateRequest(content, lyrics.getContent(), lyrics.getBackground(), status, noteType, song.getId()),
                        publisher,
                        song
                )
        );
    }
}
