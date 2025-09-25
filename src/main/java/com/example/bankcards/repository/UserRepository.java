package com.example.bankcards.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.bankcards.model.user.User;
import com.example.bankcards.model.user.vo.Email;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = 'ADMIN' AND u.id = :id")
    Optional<User> findAdminById(@Param("id") final Long id);

    Optional<User> findByEmail(final Email email);

    boolean existsByEmail(final Email email);

}
