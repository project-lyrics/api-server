# ground rule 
# table name: ~s
# using enum

CREATE TABLE `artists`
(
    `id`                     BIGINT PRIMARY KEY AUTO_INCREMENT,
    `name`                   VARCHAR(200)                      NOT NULL,
    `english_name`           VARCHAR(200)                      NULL,
    `profile_image_cdn_link` VARCHAR(2000)                     NULL,
    `status`                 ENUM ('YET', 'IN_USE', 'DELETED') NOT NULL DEFAULT 'IN_USE',
    `created_at`             DATETIME                          NOT NULL,
    `created_by`             BIGINT                            NOT NULL,
    `updated_at`             DATETIME                          NOT NULL,
    `updated_by`             BIGINT                            NOT NULL,
    `deleted_at`             DATETIME,
    `deleted_by`             BIGINT
);

CREATE TABLE `artist_members`
(
    `id`         BIGINT PRIMARY KEY AUTO_INCREMENT,
    `artist_id`  BIGINT                            NOT NULL,
    `name`       VARCHAR(100)                      NOT NULL,
    `position`   VARCHAR(200)                      NULL,
    `status`     ENUM ('YET', 'IN_USE', 'DELETED') NOT NULL DEFAULT 'IN_USE',
    `created_at` DATETIME                          NOT NULL,
    `created_by` BIGINT                            NOT NULL,
    `updated_at` DATETIME                          NOT NULL,
    `updated_by` BIGINT                            NOT NULL,
    `deleted_at` DATETIME,
    `deleted_by` BIGINT,
    INDEX `artist_members_artist_id_index` (`artist_id`)
);

CREATE TABLE `artist_instagram_ids`
(
    `id`         BIGINT PRIMARY KEY AUTO_INCREMENT,
    `artist_id`  BIGINT                            NOT NULL,
    `ig_id`      VARCHAR(100)                      NOT NULL,
    `status`     ENUM ('YET', 'IN_USE', 'DELETED') NOT NULL DEFAULT 'IN_USE',
    `created_at` DATETIME                          NOT NULL,
    `created_by` BIGINT                            NOT NULL,
    `updated_at` DATETIME                          NOT NULL,
    `updated_by` BIGINT                            NOT NULL,
    `deleted_at` DATETIME,
    `deleted_by` BIGINT,
    INDEX `artist_instagram_ids_artist_id_index` (`artist_id`)
);

CREATE TABLE `albums`
(
    `id`                   BIGINT PRIMARY KEY AUTO_INCREMENT,
    `artist_id`            BIGINT                            NOT NULL,
    `cover_image_cdn_link` VARCHAR(2000)                     NULL,
    `name`                 VARCHAR(200)                      NOT NULL,
    `publish_date`         DATE                              NOT NULL,
    `status`               ENUM ('YET', 'IN_USE', 'DELETED') NOT NULL DEFAULT 'IN_USE',
    `created_at`           DATETIME                          NOT NULL,
    `created_by`           BIGINT                            NOT NULL,
    `updated_at`           DATETIME                          NOT NULL,
    `updated_by`           BIGINT                            NOT NULL,
    `deleted_at`           DATETIME,
    `deleted_by`           BIGINT,
    INDEX `albums_artist_id_index` (`artist_id`)
);

CREATE TABLE `musics`
(
    `id`         BIGINT PRIMARY KEY AUTO_INCREMENT,
    `album_id`   BIGINT                            NOT NULL,
    `title`      VARCHAR(200)                      NOT NULL,
    `status`     ENUM ('YET', 'IN_USE', 'DELETED') NOT NULL DEFAULT 'IN_USE',
    `created_at` DATETIME                          NOT NULL,
    `created_by` BIGINT                            NOT NULL,
    `updated_at` DATETIME                          NOT NULL,
    `updated_by` BIGINT                            NOT NULL,
    `deleted_at` DATETIME,
    `deleted_by` BIGINT,
    INDEX `musics_album_id_index` (`album_id`)
);

CREATE TABLE `music_artists`
(
    `id`         BIGINT PRIMARY KEY AUTO_INCREMENT,
    `artist_id`  BIGINT                            NOT NULL,
    `music_id`   BIGINT                            NOT NULL,
    `status`     ENUM ('YET', 'IN_USE', 'DELETED') NOT NULL DEFAULT 'IN_USE',
    `created_at` DATETIME                          NOT NULL,
    `created_by` BIGINT                            NOT NULL,
    `updated_at` DATETIME                          NOT NULL,
    `updated_by` BIGINT                            NOT NULL,
    `deleted_at` DATETIME,
    `deleted_by` BIGINT,
    INDEX `music_artists_artist_id_index` (`artist_id`),
    INDEX `music_artists_music_id_index` (`music_id`)
);

CREATE TABLE `vendor_music_ids`
(
    `id`         BIGINT PRIMARY KEY AUTO_INCREMENT,
    `music_id`   BIGINT                            NOT NULL,
    `vendor`     ENUM ('SPOTIFY', 'YOUTUBE')       NOT NULL,
    `vendor_id`  VARCHAR(2000)                     NOT NULL,
    `status`     ENUM ('YET', 'IN_USE', 'DELETED') NOT NULL DEFAULT 'IN_USE',
    `created_at` DATETIME                          NOT NULL,
    `created_by` BIGINT                            NOT NULL,
    `updated_at` DATETIME                          NOT NULL,
    `updated_by` BIGINT                            NOT NULL,
    `deleted_at` DATETIME,
    `deleted_by` BIGINT,
    INDEX `vendor_music_ids_music_id_index` (`music_id`)
)
