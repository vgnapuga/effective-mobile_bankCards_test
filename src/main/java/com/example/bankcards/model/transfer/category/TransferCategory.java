package com.example.bankcards.model.transfer.category;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "categories")
public class TransferCategory {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "name", nullable = false, unique = true, updatable = false)
    @Enumerated(EnumType.STRING)
    private CategoryName name;

    public TransferCategory() {
    }

    public TransferCategory(CategoryName name) {
        this.name = java.util.Objects.requireNonNull(name);
    }

    public CategoryName getName() {
        return this.name;
    }

    @Override
    public final boolean equals(Object obj) {
        if (obj == this)
            return true;

        if (obj == null || obj.getClass() != this.getClass())
            return false;

        TransferCategory other = (TransferCategory) obj;

        if (this.id == null || other.id == null)
            return false;

        return java.util.Objects.equals(this.id, other.id);

    }

    @Override
    public final int hashCode() {
        return java.util.Objects.hash(this.id);
    }

    @Override
    public final String toString() {
        return String.format("TransferCategory{id=%d, name=%s}", this.id, this.name);
    }

}
