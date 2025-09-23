package com.example.bankcards.model.role;


import java.util.Objects;

import com.example.bankcards.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;


@Entity
@Table(name = "roles")
public class Role extends BaseEntity {

    @Column(name = "name", nullable = false, unique = true, updatable = false)
    @Enumerated(EnumType.STRING)
    private RoleName name;

    public Role() {
    }

    public Role(final RoleName name) {
        this.name = Objects.requireNonNull(name, generateNullMessageFor("name"));
    }

    public final RoleName getName() {
        return this.name;
    }

    @Override
    public final String toString() {
        return String.format("Role{id=%d, name=%s}", this.id, this.name.toString());
    }

}
