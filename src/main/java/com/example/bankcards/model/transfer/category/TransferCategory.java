package com.example.bankcards.model.transfer.category;


import com.example.bankcards.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;


@Entity
@Table(name = "categories")
public class TransferCategory extends BaseEntity {

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

    public final String toString() {
        return String.format("TransferCategory{id=%d, name=%s}", this.id, this.name);
    }

}
