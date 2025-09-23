package com.example.bankcards.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.bankcards.model.user.User;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

}
