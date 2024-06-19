package com.projectlyrics.server.domain.artist.entity.util;

import com.projectlyrics.server.domain.artist.entity.Artist;

import static com.projectlyrics.server.domain.common.util.DomainUtils.checkString;
import static com.projectlyrics.server.domain.common.util.DomainUtils.checkUrl;

public class ArtistBuilder {

    public static Artist build(String name, String englishName, String profileImageCdnLink) {
        checkString(name);
        checkString(englishName);
        checkString(profileImageCdnLink);
        checkUrl(profileImageCdnLink);

        return Artist.builder()
                .name(name)
                .englishName(englishName)
                .profileImageCdnLink(profileImageCdnLink)
                .build();
    }
}
