package com.projectlyrics.server.domain.favoriteartist.entity;

import com.projectlyrics.server.domain.artist.entity.Artist;
import com.projectlyrics.server.domain.common.entity.BaseEntity;
import com.projectlyrics.server.domain.record.domain.Record;
import com.projectlyrics.server.domain.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.projectlyrics.server.domain.common.util.DomainUtils.checkNull;

@Getter
@Entity
@Table(name = "favorite_artists")
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FavoriteArtist extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Artist artist;

    private FavoriteArtist(User user, Artist artist) {
        checkNull(user);
        checkNull(artist);
        this.user = user;
        this.artist = artist;
    }

    public static FavoriteArtist of(User user, Artist artist) {
        return new FavoriteArtist(user, artist);
    }
}
