package com.projectlyrics.server.domain.note.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.projectlyrics.server.domain.note.exception.InvalidNoteStatusException;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
public enum NoteStatus {

    DRAFT("draft"),
    PUBLISHED("published"),
    ;

    private final String type;

    @JsonValue
    public String getType() {
        return type;
    }

    @JsonCreator
    public static NoteStatus of(String type) {
        return Arrays.stream(NoteStatus.values())
                .filter(noteStatus -> noteStatus.type.equals(type))
                .findFirst()
                .orElseThrow(InvalidNoteStatusException::new);
    }
}
