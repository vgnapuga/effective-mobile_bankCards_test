package com.example.bankcards.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.bankcards.model.user.User;
import com.example.bankcards.model.user.vo.Email;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(final Email email);

    boolean existsByEmail(final Email email);

}
