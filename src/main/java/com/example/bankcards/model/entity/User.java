package com.example.bankcards.model.entity;


import com.example.bankcards.exception.DomainValidationException;
import com.example.bankcards.model.vo.user.Email;
import com.example.bankcards.model.vo.user.Password;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;


@Entity
@Table(name = "users")
public class User extends BaseEntity {

    public enum Role {
        USER, ADMIN
    }

    @Column(name = "email", nullable = false, unique = true)
    private Email email;

    @Column(name = "password", nullable = false)
    private Password password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    public User() {
    }

    public static User of(final Email email, final Password password, final Role role) {
        if (role == null)
            throw new DomainValidationException("User role is <null>");

        return new User(email, password, role);
    }

    private User(final Email email, final Password password, final Role role) {

        this.email = email;
        this.password = password;
        this.role = role;
    }

    public final void changeEmail(final Email newEmail) {
        this.email = newEmail;
    }

    public final void changePassword(final Password newPassword) {
        this.password = newPassword;
    }

    public final void changeRole(final Role newRole) {
        if (newRole == null)
            throw new DomainValidationException("User new role is <null>");

        this.role = newRole;
    }

    public final Email getEmail() {
        return this.email;
    }

    public final Password getPassword() {
        return this.password;
    }

    public final Role getRole() {
        return this.role;
    }

    @Override
    public final String toString() {
        return String.format(
                "User{id=%d, email=%s, password=***, role=%s}",
                this.id,
                this.email.toString(),
                this.role.toString());
    }
}
