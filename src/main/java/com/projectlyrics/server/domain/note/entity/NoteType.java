package com.projectlyrics.server.domain.note.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
public enum NoteType {

    FREE("FREE"),
    QUESTION("QUESTION"),
    LYRICS_ANALYSIS("LYRICS_ANALYSIS"),
    ;

    private final String type;

    @JsonValue
    public String getType() {
        return type;
    }

    @JsonCreator
    public static NoteType of(String type) {
        return Arrays.stream(NoteType.values())
                .filter(noteType -> noteType.type.equals(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid NoteType: " + type));
    }
}
