package com.projectlyrics.server.domain.common.entity;

import com.projectlyrics.server.domain.common.entity.enumerate.EntityStatusEnum;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import java.time.Clock;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public abstract class BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Setter
    private EntityStatusEnum status;

    @CreatedDate
    private LocalDateTime createdAt;

    // TODO: user entity 생성 후 type 변경할 것.
    @CreatedBy
    private long createdBy;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // TODO: user entity 생성 후 type 변경할 것.
    @LastModifiedBy
    private long updatedBy;

    @Column(nullable = true)
    private LocalDateTime deletedAt;

    // TODO: user entity 생성 후 type 변경할 것.
    @Column(nullable = true)
    private Long deletedBy;

    @Value("${admin}")
    @Transient
    private Long adminUserId;

    protected BaseEntity() {this.status = EntityStatusEnum.IN_USE;}

    public boolean isInUse() {
        return EntityStatusEnum.IN_USE.equals(this.status);
    }

    public void delete(long deletedById, Clock clock) {
        this.status = EntityStatusEnum.DELETED;
        this.deletedAt = LocalDateTime.now(clock);
        this.deletedBy = deletedById;
    }

    public void forcedDelete(Clock clock) {
        this.status = EntityStatusEnum.FORCED_WITHDRAWAL;
        this.deletedAt = LocalDateTime.now(clock);
        this.deletedBy = adminUserId; //admin
    }

    public void restore() {
        this.status = EntityStatusEnum.IN_USE;
        this.deletedAt = null;
        this.deletedBy = null;
    }
}
