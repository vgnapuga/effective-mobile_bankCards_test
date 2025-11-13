package com.example.bankcards.model;


import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;


@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    protected final String generateNullMessageFor(final String attributeName) {
        return String.format("%s %s is <null>", this.getClass().getSimpleName(), attributeName.toLowerCase());
    }

    public Long getId() {
        return this.id;
    }

    public LocalDateTime getCreationTime() {
        return this.createdAt;
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
