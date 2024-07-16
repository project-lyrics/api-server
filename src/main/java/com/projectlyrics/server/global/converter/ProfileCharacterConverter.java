package com.projectlyrics.server.global.converter;

import com.projectlyrics.server.domain.user.entity.ProfileCharacter;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.GenericConverter;

public class ProfileCharacterConverter implements Converter<String, ProfileCharacter> {
    @Override
    public ProfileCharacter convert(String source) {
        return ProfileCharacter.of(source);
    }
}
