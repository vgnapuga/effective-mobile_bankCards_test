package com.example.bankcards.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.bankcards.model.role.Role;


@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

}
