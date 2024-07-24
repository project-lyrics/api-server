package com.projectlyrics.server.domain.note.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.projectlyrics.server.domain.note.exception.InvalidNoteBackgroundException;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
public enum NoteBackground {

    WHITE("white"),
    ;

    private final String type;

    @JsonValue
    public String getType() {
        return type;
    }

    @JsonCreator
    public static NoteBackground of(String type) {
        return Arrays.stream(NoteBackground.values())
                .filter(noteBackground -> noteBackground.type.equals(type))
                .findFirst()
                .orElseThrow(InvalidNoteBackgroundException::new);
    }
}
