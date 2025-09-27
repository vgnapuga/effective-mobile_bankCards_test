package com.example.bankcards.service;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.model.role.Role;
import com.example.bankcards.model.role.RoleName;
import com.example.bankcards.repository.RoleRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
@RequiredArgsConstructor
public class RoleService {

    private static final String PART_OF_NOT_FOUND_MESSAGE = " role not found in system";

    private final RoleRepository roleRepository;

    private Role findRoleByName(RoleName roleName) {
        return roleRepository.findByName(roleName).orElseThrow(
                () -> new ResourceNotFoundException(roleName.toString() + PART_OF_NOT_FOUND_MESSAGE));
    }

    @Transactional(readOnly = true)
    public Role getUserRole() {
        return findRoleByName(RoleName.USER);
    }

    @Transactional(readOnly = true)
    public Role getAdminRole() {
        return findRoleByName(RoleName.ADMIN);
    }

}
