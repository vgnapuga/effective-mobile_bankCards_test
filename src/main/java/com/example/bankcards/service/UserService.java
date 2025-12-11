package com.example.bankcards.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.bankcards.dto.user.request.UserCreateRequest;
import com.example.bankcards.dto.user.request.UserUpdateEmailRequest;
import com.example.bankcards.dto.user.request.UserUpdatePasswordRequest;
import com.example.bankcards.exception.AccessDeniedException;
import com.example.bankcards.exception.BusinessRuleViolationException;
import com.example.bankcards.exception.ResourceAlreadyExistsException;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.model.user.Role;
import com.example.bankcards.model.user.User;
import com.example.bankcards.model.user.vo.Email;
import com.example.bankcards.model.user.vo.Password;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.constant.UserConstants;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserService extends BaseService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // ---------- Helper methods ---------- //

    private final void checkEmailUniqueness(final Email email) {
        if (userRepository.existsByEmail(email))
            throw new ResourceAlreadyExistsException(UserConstants.Email.alreadyExistsMessage(email.getValue()));
    }

    private static void validateRawPassword(final String rawPassword) {
        if (rawPassword.length() < UserConstants.Password.RAW_PASSWORD_MIN_SIZE)
            throw new BusinessRuleViolationException(
                    UserConstants.Password.servicePasswordInvalidLengthMessage(rawPassword.length()));
    }

    public void checkAdminPermissionTo(final String operationName, Long adminId) {
        User admin = findUserById(adminId);
        if (!admin.isAdmin())
            throw new AccessDeniedException(String.format("Permission to %s denied for id=%d", operationName, adminId));
    }

    public User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException(String.format("User with id=%d not found", userId)));
    }

    public User findUserByEmail(final String email) {
        return userRepository.findByEmail(new Email(email)).orElseThrow(
                () -> new ResourceNotFoundException(String.format("User with Email=%s not found", email)));
    }

    // ------------------------------------ //

    @Transactional
    public User createUser(Long adminId, final UserCreateRequest request) {
        validateId(adminId);
        checkAdminPermissionTo("create new user", adminId);

        Email email = new Email(request.email());
        checkEmailUniqueness(email);

        String rawPassword = request.password();
        validateRawPassword(rawPassword);

        String hashedPassword = passwordEncoder.encode(rawPassword);
        Password password = new Password(hashedPassword);

        Role userRole = Role.USER;
        User user = new User(email, password, userRole);

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User getUserByIdForAdmin(Long adminId, Long userId) {
        validateId(adminId);
        validateId(userId);

        checkAdminPermissionTo("get user", adminId);

        return findUserById(userId);
    }

    @Transactional(readOnly = true)
    public User getCurrentUserProfile(Long userId) {
        validateId(userId);
        return findUserById(userId);
    }

    @Transactional(readOnly = true)
    public Page<User> getAllUsers(Long adminId, final Pageable pageable) {
        validatePagination(pageable);
        validateId(adminId);
        checkAdminPermissionTo("get all users", adminId);

        return userRepository.findAll(pageable);
    }

    @Transactional
    public User updateUserEmail(Long userId, final UserUpdateEmailRequest request) {
        validateId(userId);

        Email newEmail = new Email(request.email());
        checkEmailUniqueness(newEmail);

        User user = findUserById(userId);
        user.changeEmail(newEmail);

        return userRepository.save(user);
    }

    @Transactional
    public User updateUserPassword(Long userId, final UserUpdatePasswordRequest request) {
        validateId(userId);

        String rawNewPassword = request.password();
        validateRawPassword(rawNewPassword);

        String hashedNewPassword = passwordEncoder.encode(rawNewPassword);
        Password newPassword = new Password(hashedNewPassword);

        User user = findUserById(userId);
        user.changePassword(newPassword);

        return userRepository.save(user);
    }

    @Transactional
    public void deleteUserById(Long adminId, Long userId) {
        validateId(adminId);
        validateId(userId);

        checkAdminPermissionTo("delete user", adminId);

        User userToDelete = findUserById(userId);
        userRepository.delete(userToDelete);
    }

}
