package com.projectlyrics.server.domain.banner.domain;

import com.projectlyrics.server.domain.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Entity
@Table(name = "banners")
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Banner extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageUrl;
    private String redirectUrl;
    @Setter
    private LocalDateTime startDate;
    private LocalDateTime dueDate;

    private Banner(
            String imageUrl,
            String redirectUrl,
            LocalDateTime startDate,
            LocalDateTime dueDate
    ) {
        this.imageUrl = imageUrl;
        this.redirectUrl = redirectUrl;
        this.startDate = startDate;
        this.dueDate = dueDate;
    }

    public static Banner create(BannerCreate bannerCreate) {
        return new Banner(
                bannerCreate.imageUrl(),
                bannerCreate.redirectUrl(),
                bannerCreate.startDate(),
                bannerCreate.dueDate()
        );
    }

    public static Banner createWithId(Long id, BannerCreate bannerCreate) {
        return new Banner(
                id,
                bannerCreate.imageUrl(),
                bannerCreate.redirectUrl(),
                bannerCreate.startDate(),
                bannerCreate.dueDate()
        );
    }
}
