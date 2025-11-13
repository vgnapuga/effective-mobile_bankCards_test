package com.example.bankcards.model;


import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PreUpdate;


@MappedSuperclass
public abstract class UpdatableEntity extends BaseEntity {

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP", nullable = true, updatable = true)
    private LocalDateTime updatedAt;

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getUpdateTime() {
        return this.updatedAt;
    }

}
