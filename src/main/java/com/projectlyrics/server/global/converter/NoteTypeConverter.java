package com.projectlyrics.server.global.converter;

import com.projectlyrics.server.domain.note.entity.NoteType;
import org.springframework.core.convert.converter.Converter;

public class NoteTypeConverter implements Converter<String, NoteType> {
    @Override
    public NoteType convert(String source) {
        return NoteType.of(source);
    }
}
