package com.example.bankcards.model;


import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;


@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP", nullable = false, updatable = true)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    protected final String generateNullMessageFor(final String attributeName) {
        return String.format("%s %s is <null>", this.getClass().getSimpleName(), attributeName.toLowerCase());
    }

    public final Long getId() {
        return this.id;
    }

    public final LocalDateTime getCreationTime() {
        return this.createdAt;
    }

    public final LocalDateTime getUpdateTime() {
        return this.updatedAt;
    }

    @Override
    public final boolean equals(Object obj) {
        if (obj == this)
            return true;

        if (obj == null || obj.getClass() != this.getClass())
            return false;

        BaseEntity other = (BaseEntity) obj;

        if (this.id == null || other.id == null)
            return false;

        return java.util.Objects.equals(this.id, other.id);

    }

    @Override
    public final int hashCode() {
        return java.util.Objects.hash(this.id);
    }

    @Override
    public abstract String toString();

}
