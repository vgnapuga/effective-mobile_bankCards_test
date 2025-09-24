package com.example.bankcards.model.user;


import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.example.bankcards.exception.BusinessRuleViolationException;
import com.example.bankcards.exception.DomainValidationException;
import com.example.bankcards.model.BaseEntity;
import com.example.bankcards.model.role.Role;
import com.example.bankcards.model.role.RoleName;
import com.example.bankcards.model.user.converter.EmailConverter;
import com.example.bankcards.model.user.converter.PasswordConverter;
import com.example.bankcards.model.user.vo.Email;
import com.example.bankcards.model.user.vo.Password;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;


@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Column(name = "email", nullable = false, unique = true)
    @Convert(converter = EmailConverter.class)
    private Email email;

    @Column(name = "password", nullable = false)
    @Convert(converter = PasswordConverter.class)
    private Password password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    public User() {
    }

    public static User of(final Email email, final Password password, final Set<Role> roles) {
        validateRoles(roles);
        return new User(email, password, roles);
    }

    private User(final Email email, final Password password, final Set<Role> roles) {
        this.email = Objects.requireNonNull(email, generateNullMessageFor("email"));
        this.password = Objects.requireNonNull(password, generateNullMessageFor("password"));
        this.roles = roles;
    }

    public User(final Email email, final Password password, final Role role) {
        this.email = Objects.requireNonNull(email, generateNullMessageFor("email"));
        this.password = Objects.requireNonNull(password, generateNullMessageFor("password"));
        this.roles.add(Objects.requireNonNull(role, generateNullMessageFor("role")));
    }

    public static final void validateRoles(final Set<Role> roles) {
        if (roles == null)
            throw new DomainValidationException("User roles is <null>");

        if (roles.isEmpty())
            throw new BusinessRuleViolationException("User must have at least 1 role");
    }

    public final void giveAdminRole() {
        this.roles.add(new Role(RoleName.ADMIN));
    }

    public final void removeAdminRole() {
        this.roles.removeIf(role -> role.getName().equals(RoleName.ADMIN));
    }

    public final boolean isAdmin() {
        return isHasRole(RoleName.ADMIN);
    }

    private boolean isHasRole(final RoleName roleToCheck) {
        for (Role role : roles) {
            if (role.getName() == roleToCheck)
                return true;
        }

        return false;
    }

    public final void changeEmail(final Email newEmail) {
        this.email = Objects.requireNonNull(newEmail, generateNullMessageFor("new email"));
    }

    public final void changePassword(final Password newPassword) {
        this.password = Objects.requireNonNull(newPassword, generateNullMessageFor("new password"));
    }

    public final void changeRoles(final Set<Role> newRoles) {
        validateRoles(newRoles);

        this.roles.clear();
        this.roles.addAll(newRoles);
    }

    public final void addRole(final Role newRole) {
        this.roles.add(Objects.requireNonNull(newRole, generateNullMessageFor("role")));
    }

    public final void removeRole(final Role roleToRemove) {
        if (this.roles.size() == 1)
            throw new BusinessRuleViolationException("User must have at least 1 role");

        this.roles.remove(roleToRemove);
    }

    public final Email getEmail() {
        return this.email;
    }

    public final Password getPassword() {
        return this.password;
    }

    public final Set<Role> getRoles() {
        return new HashSet<>(this.roles);
    }

    @Override
    public final String toString() {
        return String.format(
                "User{id=%d, email=%s, password=***, roles=%s}",
                this.id,
                this.email.toString(),
                this.roles.toString());
    }
}
