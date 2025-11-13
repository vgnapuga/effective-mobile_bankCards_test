package com.example.bankcards.model.user;


import java.util.Objects;

import com.example.bankcards.model.UpdatableEntity;
import com.example.bankcards.model.user.converter.EmailConverter;
import com.example.bankcards.model.user.converter.PasswordConverter;
import com.example.bankcards.model.user.vo.Email;
import com.example.bankcards.model.user.vo.Password;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;


@Entity
@Table(name = "users")
public class User extends UpdatableEntity {

    @Column(name = "email", nullable = false, unique = true)
    @Convert(converter = EmailConverter.class)
    private Email email;

    @Column(name = "password_hashed", nullable = false)
    @Convert(converter = PasswordConverter.class)
    private Password password;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    public User() {
    }

    public static User of(final Email email, final Password password, Role role) {
        return new User(email, password, role);
    }

    public User(final Email email, final Password password, final Role role) {
        this.email = Objects.requireNonNull(email, generateNullMessageFor("email"));
        this.password = Objects.requireNonNull(password, generateNullMessageFor("password"));
        this.role = Objects.requireNonNull(role, generateNullMessageFor("role"));
    }

    public final boolean isAdmin() {
        return this.role == Role.ADMIN;
    }

    public final void changeEmail(final Email newEmail) {
        this.email = Objects.requireNonNull(newEmail, generateNullMessageFor("new email"));
    }

    public final void changePassword(final Password newPassword) {
        this.password = Objects.requireNonNull(newPassword, generateNullMessageFor("new password"));
    }

    public Email getEmail() {
        return this.email;
    }

    public Password getPassword() {
        return this.password;
    }

    public Role getRole() {
        return this.role;
    }

    @Override
    public String toString() {
        return String.format(
                "User{id=%d, email=%s, password=***, roles=%s}",
                this.id,
                this.email.toString(),
                this.role);
    }
}
