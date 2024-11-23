package com.projectlyrics.server.domain.block.domain;

import com.projectlyrics.server.domain.common.entity.BaseEntity;
import com.projectlyrics.server.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "blocks")
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Block extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocker_id")
    private User blocker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocked_id")
    private User blocked;

    private Block(
            Long id,
            User blocker,
            User blocked
    ) {
        this.id = id;
        this.blocker = blocker;
        this.blocked = blocked;
    }

    public Block(
            User blocker,
            User blocked
    ) {
        this(null, blocker, blocked);
    }
}
