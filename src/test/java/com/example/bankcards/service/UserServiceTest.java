package com.example.bankcards.service;


import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.bankcards.dto.user.request.UserCreateRequest;
import com.example.bankcards.dto.user.request.UserUpdateEmailRequest;
import com.example.bankcards.dto.user.request.UserUpdatePasswordRequest;
import com.example.bankcards.exception.AccessDeniedException;
import com.example.bankcards.exception.BusinessRuleViolationException;
import com.example.bankcards.exception.DomainValidationException;
import com.example.bankcards.exception.ResourceAlreadyExistsException;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.model.user.Role;
import com.example.bankcards.model.user.User;
import com.example.bankcards.model.user.vo.Email;
import com.example.bankcards.model.user.vo.Password;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.contract.create.AdminCreateBehaviorTest;
import com.example.bankcards.service.contract.delete.AdminDeleteBehaviorTest;
import com.example.bankcards.service.contract.read.AdminReadBehaviorTest;
import com.example.bankcards.service.contract.read.ReadBehaviorTest;
import com.example.bankcards.service.contract.update.UpdateBehaviorTest;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private static final Long TEST_ADMIN_ID = 1L;
    private static final Long TEST_USER_ID = 2L;
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_RAW_PASSWORD = "password123";
    private static final String TEST_HASHED_PASSWORD = "$2a$10$validBcryptHashWith60Characters1234567890123456781234";

    // ---------- Helper methods ---------- //

    private User createTestUser() {
        return new User(new Email(TEST_EMAIL), new Password(TEST_HASHED_PASSWORD), Role.USER);
    }

    private User createTestAdmin() {
        User admin = new User(new Email("admin@example.com"), new Password(TEST_HASHED_PASSWORD), Role.ADMIN);
        admin.giveAdminRole();
        return admin;
    }

    private void whenFindById(Optional<User> optional) {
        when(userRepository.findById(anyLong())).thenReturn(optional);
    }

    private void whenExistsByEmail(boolean exists) {
        when(userRepository.existsByEmail(any(Email.class))).thenReturn(exists);
    }

    private void whenPasswordEncode() {
        when(passwordEncoder.encode(TEST_RAW_PASSWORD)).thenReturn(TEST_HASHED_PASSWORD);
    }

    private void whenSave() {
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    // ------------------------------------ //

    @Nested
    class CreateUserTests implements AdminCreateBehaviorTest {

        @Test
        @Override
        public void shouldReturnEntity_whenValidRequest() {
            // Given
            whenExistsByEmail(false);
            whenPasswordEncode();
            whenSave();

            UserCreateRequest request = new UserCreateRequest(TEST_EMAIL, TEST_RAW_PASSWORD);

            // When
            User result = userService.createUser(TEST_ADMIN_ID, request);

            // Then
            assertNotNull(result);
            assertEquals(TEST_EMAIL, result.getEmail().getValue());

            verify(userRepository).existsByEmail(any(Email.class));
            verify(passwordEncoder).encode(TEST_RAW_PASSWORD);
            verify(userRepository).save(any(User.class));
            verifyNoMoreInteractions(userRepository, passwordEncoder);
        }

        @Test
        @Override
        public void shouldThrowException_whenNullId() {
            // Given
            UserCreateRequest request = new UserCreateRequest(TEST_EMAIL, TEST_RAW_PASSWORD);

            // When
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> userService.createUser(null, request));

            // Then
            assertEquals("Entity id is <null>", exception.getMessage());
            verifyNoInteractions(userRepository, passwordEncoder);
        }

        @ParameterizedTest
        @ValueSource(longs = { 0L, -1L, -100L })
        @Override
        public void shouldThrowException_whenInvalidId(Long invalidId) {
            // Given
            UserCreateRequest request = new UserCreateRequest(TEST_EMAIL, TEST_RAW_PASSWORD);

            // When
            BusinessRuleViolationException exception = assertThrows(
                    BusinessRuleViolationException.class,
                    () -> userService.createUser(invalidId, request));

            // Then
            assertTrue(exception.getMessage().contains("Entity id must be positive"));
            verifyNoInteractions(userRepository, passwordEncoder);
        }

        @Test
        @Override
        public void shouldThrowException_whenNotAdmin() {
            // Given
            UserCreateRequest request = new UserCreateRequest(TEST_EMAIL, TEST_RAW_PASSWORD);

            // When
            AccessDeniedException exception = assertThrows(
                    AccessDeniedException.class,
                    () -> userService.createUser(TEST_ADMIN_ID, request));

            // Then
            assertTrue(exception.getMessage().contains("Permission to create new user denied"));
            verifyNoMoreInteractions(userRepository);
            verifyNoInteractions(passwordEncoder);
        }

        @Test
        void shouldThrowException_whenEmailAlreadyExists() {
            // Given
            whenExistsByEmail(true);

            UserCreateRequest request = new UserCreateRequest(TEST_EMAIL, TEST_RAW_PASSWORD);

            // When
            ResourceAlreadyExistsException exception = assertThrows(
                    ResourceAlreadyExistsException.class,
                    () -> userService.createUser(TEST_ADMIN_ID, request));

            // Then
            assertTrue(exception.getMessage().contains("already exists"));
            verify(userRepository).existsByEmail(any(Email.class));
            verifyNoMoreInteractions(userRepository);
            verifyNoInteractions(passwordEncoder);
        }

        @ParameterizedTest
        @ValueSource(strings = { "123", "pass", "1234567" })
        void shouldThrowException_whenPasswordTooShort(String shortPassword) {
            // Given
            whenFindAdminById(Optional.of(createTestAdmin()));
            whenExistsByEmail(false);

            UserCreateRequest request = new UserCreateRequest(TEST_EMAIL, shortPassword);

            // When
            BusinessRuleViolationException exception = assertThrows(
                    BusinessRuleViolationException.class,
                    () -> userService.createUser(TEST_ADMIN_ID, request));

            // Then
            assertTrue(exception.getMessage().contains("Invalid raw password length"));
            verify(userRepository).existsByEmail(any(Email.class));
            verifyNoMoreInteractions(userRepository);
            verifyNoInteractions(passwordEncoder);
        }

    }

    @Nested
    class GetUserByIdForAdminTests implements AdminReadBehaviorTest {

        @Test
        @Override
        public void shouldReturnEntity_whenValidRequest() {
            // Given
            User testUser = createTestUser();
            whenFindAdminById(Optional.of(createTestAdmin()));
            whenFindById(Optional.of(testUser));

            // When
            User result = userService.getUserByIdForAdmin(TEST_ADMIN_ID, TEST_USER_ID);

            // Then
            assertNotNull(result);
            assertEquals(TEST_EMAIL, result.getEmail().getValue());

            verify(userRepository).findById(TEST_USER_ID);
            verifyNoMoreInteractions(userRepository);
        }

        @Test
        @Override
        public void shouldThrowException_whenNullRequesterId() {
            // When
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> userService.getUserByIdForAdmin(null, TEST_USER_ID));

            // Then
            assertEquals("Entity id is <null>", exception.getMessage());
            verifyNoInteractions(userRepository);
        }

        @Test
        @Override
        public void shouldThrowException_whenNullIdToRead() {
            // When
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> userService.getUserByIdForAdmin(TEST_ADMIN_ID, null));

            // Then
            assertEquals("Entity id is <null>", exception.getMessage());
            verifyNoInteractions(userRepository);
        }

        @ParameterizedTest
        @ValueSource(longs = { 0L, -1L, -100L })
        @Override
        public void shouldThrowException_whenInvalidRequesterId(Long invalidId) {
            // When
            BusinessRuleViolationException exception = assertThrows(
                    BusinessRuleViolationException.class,
                    () -> userService.getUserByIdForAdmin(invalidId, TEST_USER_ID));

            // Then
            assertTrue(exception.getMessage().contains("Entity id must be positive"));
            verifyNoInteractions(userRepository);
        }

        @ParameterizedTest
        @ValueSource(longs = { 0L, -1L, -100L })
        @Override
        public void shouldThrowException_whenInvalidIdToRead(Long invalidId) {
            // When
            BusinessRuleViolationException exception = assertThrows(
                    BusinessRuleViolationException.class,
                    () -> userService.getUserByIdForAdmin(TEST_ADMIN_ID, invalidId));

            // Then
            assertTrue(exception.getMessage().contains("Entity id must be positive"));
            verifyNoInteractions(userRepository);
        }

        @Test
        @Override
        public void shouldThrowException_whenNotAdmin() {
            // When
            AccessDeniedException exception = assertThrows(
                    AccessDeniedException.class,
                    () -> userService.getUserByIdForAdmin(TEST_ADMIN_ID, TEST_USER_ID));

            // Then
            assertTrue(exception.getMessage().contains("Permission to get user denied"));
            verifyNoMoreInteractions(userRepository);
        }

        @Test
        @Override
        public void shouldThrowException_whenEntityNotFound() {
            // Given
            whenFindAdminById(Optional.of(createTestAdmin()));
            whenFindById(Optional.empty());

            // When & Then
            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> userService.getUserByIdForAdmin(TEST_ADMIN_ID, TEST_USER_ID));

            assertTrue(exception.getMessage().contains("not found"));
            verify(userRepository).findById(TEST_USER_ID);
            verifyNoMoreInteractions(userRepository);
        }

    }

    @Nested
    class GetCurrentUserProfileTests implements ReadBehaviorTest {

        @Test
        @Override
        public void shouldReturnEntity_whenValidRequest() {
            // Given
            User testUser = createTestUser();
            whenFindById(Optional.of(testUser));

            // When
            User result = userService.getCurrentUserProfile(TEST_USER_ID);

            // Then
            assertNotNull(result);
            assertEquals(TEST_EMAIL, result.getEmail().getValue());
            verify(userRepository).findById(TEST_USER_ID);
            verifyNoMoreInteractions(userRepository);
        }

        @Test
        @Override
        public void shouldThrowException_whenNullRequesterId() {
            // When
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> userService.getCurrentUserProfile(null));

            // Then
            assertEquals("Entity id is <null>", exception.getMessage());
            verifyNoInteractions(userRepository);
        }

        @Override
        public void shouldThrowException_whenNullIdToRead() {
        }

        @ParameterizedTest
        @ValueSource(longs = { 0L, -1L, -100L })
        @Override
        public void shouldThrowException_whenInvalidRequesterId(Long invalidId) {
            // When
            BusinessRuleViolationException exception = assertThrows(
                    BusinessRuleViolationException.class,
                    () -> userService.getCurrentUserProfile(invalidId));

            // Then
            assertTrue(exception.getMessage().contains("Entity id must be positive"));
            verifyNoInteractions(userRepository);
        }

        @Override
        public void shouldThrowException_whenInvalidIdToRead(Long id) {
        }

        @Test
        @Override
        public void shouldThrowException_whenEntityNotFound() {
            // Given
            whenFindById(Optional.empty());

            // When
            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> userService.getCurrentUserProfile(TEST_USER_ID));

            // Then
            assertTrue(exception.getMessage().contains("not found"));
            verify(userRepository).findById(TEST_USER_ID);
            verifyNoMoreInteractions(userRepository);
        }

    }

    @Nested
    class UpdateUserEmailTests implements UpdateBehaviorTest {

        @Test
        @Override
        public void shouldReturnEntity_whenValidRequest() {
            // Given
            String newEmail = "new@example.com";
            User testUser = createTestUser();

            whenFindById(Optional.of(testUser));
            whenExistsByEmail(false);
            whenSave();

            UserUpdateEmailRequest request = new UserUpdateEmailRequest(newEmail);

            // When
            User result = userService.updateUserEmail(TEST_USER_ID, request);

            // Then
            assertNotNull(result);
            assertEquals(newEmail, result.getEmail().getValue());

            verify(userRepository).findById(TEST_USER_ID);
            verify(userRepository).existsByEmail(any(Email.class));
            verify(userRepository).save(any(User.class));
            verifyNoMoreInteractions(userRepository);
        }

        @Test
        @Override
        public void shouldThrowException_whenNullRequesterId() {
            // Given
            UserUpdateEmailRequest request = new UserUpdateEmailRequest(TEST_EMAIL);

            // When
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> userService.updateUserEmail(null, request));

            // Then
            assertEquals("Entity id is <null>", exception.getMessage());
            verifyNoInteractions(userRepository);
        }

        @ParameterizedTest
        @ValueSource(longs = { 0L, -1L, -100L })
        @Override
        public void shouldThrowException_whenInvalidRequesterId(Long invalidId) {
            // Given
            UserUpdateEmailRequest request = new UserUpdateEmailRequest(TEST_EMAIL);

            // When
            BusinessRuleViolationException exception = assertThrows(
                    BusinessRuleViolationException.class,
                    () -> userService.updateUserEmail(invalidId, request));

            // Then
            assertTrue(exception.getMessage().contains("Entity id must be positive"));
            verifyNoInteractions(userRepository);
        }

        @Test
        @Override
        public void shouldThrowException_whenEntityNotFound() {
            // Given
            whenFindById(Optional.empty());
            whenExistsByEmail(false);

            UserUpdateEmailRequest request = new UserUpdateEmailRequest(TEST_EMAIL);

            // When
            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> userService.updateUserEmail(TEST_USER_ID, request));

            // Then
            assertTrue(exception.getMessage().contains("not found"));
            verify(userRepository).existsByEmail(any(Email.class));
            verify(userRepository).findById(TEST_USER_ID);
            verifyNoMoreInteractions(userRepository);
        }

        @Test
        void shouldThrowException_whenEmailAlreadyExists() {
            // Given
            whenExistsByEmail(true);

            UserUpdateEmailRequest request = new UserUpdateEmailRequest("existing@example.com");

            // When
            ResourceAlreadyExistsException exception = assertThrows(
                    ResourceAlreadyExistsException.class,
                    () -> userService.updateUserEmail(TEST_USER_ID, request));

            // Then
            assertTrue(exception.getMessage().contains("already exists"));
            verify(userRepository).existsByEmail(any(Email.class));
            verifyNoMoreInteractions(userRepository);
        }

    }

    @Nested
    class UpdateUserPasswordTests implements UpdateBehaviorTest {

        @Test
        @Override
        public void shouldReturnEntity_whenValidRequest() {
            // Given
            User testUser = createTestUser();
            whenFindById(Optional.of(testUser));
            whenPasswordEncode();
            whenSave();

            UserUpdatePasswordRequest request = new UserUpdatePasswordRequest(TEST_RAW_PASSWORD);

            // When
            User result = userService.updateUserPassword(TEST_USER_ID, request);

            // Then
            assertNotNull(result);
            verify(userRepository).findById(TEST_USER_ID);
            verify(passwordEncoder).encode(TEST_RAW_PASSWORD);
            verify(userRepository).save(any(User.class));
            verifyNoMoreInteractions(userRepository, passwordEncoder);
        }

        @Test
        @Override
        public void shouldThrowException_whenNullRequesterId() {
            // Given
            UserUpdatePasswordRequest request = new UserUpdatePasswordRequest(TEST_RAW_PASSWORD);

            // When
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> userService.updateUserPassword(null, request));

            // Then
            assertEquals("Entity id is <null>", exception.getMessage());
            verifyNoInteractions(userRepository, passwordEncoder);
        }

        @ParameterizedTest
        @ValueSource(longs = { 0L, -1L, -100L })
        @Override
        public void shouldThrowException_whenInvalidRequesterId(Long invalidId) {
            // Given
            UserUpdatePasswordRequest request = new UserUpdatePasswordRequest(TEST_RAW_PASSWORD);

            // When
            BusinessRuleViolationException exception = assertThrows(
                    BusinessRuleViolationException.class,
                    () -> userService.updateUserPassword(invalidId, request));

            // Then
            assertTrue(exception.getMessage().contains("Entity id must be positive"));
            verifyNoInteractions(userRepository, passwordEncoder);
        }

        @Test
        @Override
        public void shouldThrowException_whenEntityNotFound() {
            // Given
            whenPasswordEncode();
            whenFindById(Optional.empty());

            UserUpdatePasswordRequest request = new UserUpdatePasswordRequest(TEST_RAW_PASSWORD);

            // When
            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> userService.updateUserPassword(TEST_USER_ID, request));

            // Then
            assertTrue(exception.getMessage().contains("not found"));
            verify(passwordEncoder).encode(TEST_RAW_PASSWORD);
            verify(userRepository).findById(TEST_USER_ID);
            verifyNoMoreInteractions(userRepository, passwordEncoder);
        }

        @ParameterizedTest
        @ValueSource(strings = { "123", "short", "1234567" })
        void shouldThrowException_whenPasswordTooShort(String shortPassword) {
            // Given
            UserUpdatePasswordRequest request = new UserUpdatePasswordRequest(shortPassword);

            // When
            BusinessRuleViolationException exception = assertThrows(
                    BusinessRuleViolationException.class,
                    () -> userService.updateUserPassword(TEST_USER_ID, request));

            // Then
            assertTrue(exception.getMessage().contains("Invalid raw password length"));
            verifyNoInteractions(passwordEncoder, userRepository);
        }

    }

    @Nested
    class DeleteUserByIdTests implements AdminDeleteBehaviorTest {

        @Test
        @Override
        public void shouldNotThrowException_whenValidRequest() {
            // Given
            User testUser = createTestUser();
            whenFindAdminById(Optional.of(createTestAdmin()));
            whenFindById(Optional.of(testUser));

            // When
            assertDoesNotThrow(() -> userService.deleteUserById(TEST_ADMIN_ID, TEST_USER_ID));

            // Then
            verify(userRepository).findById(TEST_USER_ID);
            verify(userRepository).delete(testUser);
            verifyNoMoreInteractions(userRepository);
        }

        @Test
        @Override
        public void shouldThrowException_whenNullRequesterId() {
            // When
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> userService.deleteUserById(null, TEST_USER_ID));

            // Then
            assertEquals("Entity id is <null>", exception.getMessage());
            verifyNoInteractions(userRepository);
        }

        @Test
        @Override
        public void shouldThrowException_whenNullIdToDelete() {
            // When
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> userService.deleteUserById(TEST_ADMIN_ID, null));

            // Then
            assertEquals("Entity id is <null>", exception.getMessage());
            verifyNoInteractions(userRepository);
        }

        @ParameterizedTest
        @ValueSource(longs = { 0L, -1L, -100L })
        @Override
        public void shouldThrowException_whenInvalidRequesterId(Long invalidId) {
            // When
            BusinessRuleViolationException exception = assertThrows(
                    BusinessRuleViolationException.class,
                    () -> userService.deleteUserById(invalidId, TEST_USER_ID));

            // Then
            assertTrue(exception.getMessage().contains("Entity id must be positive"));
            verifyNoInteractions(userRepository);
        }

        @ParameterizedTest
        @ValueSource(longs = { 0L, -1L, -100L })
        @Override
        public void shouldThrowException_whenInvalidIdToDelete(Long invalidId) {
            // When
            BusinessRuleViolationException exception = assertThrows(
                    BusinessRuleViolationException.class,
                    () -> userService.deleteUserById(TEST_ADMIN_ID, invalidId));

            // Then
            assertTrue(exception.getMessage().contains("Entity id must be positive"));
            verifyNoInteractions(userRepository);
        }

        @Test
        @Override
        public void shouldThrowException_whenNotAdmin() {
            // When
            AccessDeniedException exception = assertThrows(
                    AccessDeniedException.class,
                    () -> userService.deleteUserById(TEST_ADMIN_ID, TEST_USER_ID));

            // Then
            assertTrue(exception.getMessage().contains("Permission to delete user denied"));
            verifyNoMoreInteractions(userRepository);
        }

        @Test
        @Override
        public void shouldThrowException_whenEntityNotFound() {
            // Given
            whenFindAdminById(Optional.of(createTestAdmin()));
            whenFindById(Optional.empty());

            // When
            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> userService.deleteUserById(TEST_ADMIN_ID, TEST_USER_ID));

            // Then
            assertTrue(exception.getMessage().contains("not found"));
            verify(userRepository).findById(TEST_USER_ID);
            verifyNoMoreInteractions(userRepository);
        }

    }

    @Nested
    class GetAllUsersTests {

        @Test
        void shouldReturnUserPage_whenValidRequest() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);
            Page<User> userPage = new PageImpl<>(new ArrayList<>());

            whenFindAdminById(Optional.of(createTestAdmin()));
            when(userRepository.findAll(pageable)).thenReturn(userPage);

            // When
            Page<User> result = userService.getAllUsers(TEST_ADMIN_ID, pageable);

            // Then
            assertNotNull(result);
            verify(userRepository).findAll(pageable);
            verifyNoMoreInteractions(userRepository);
        }

        @Test
        void shouldThrowException_whenNullPageable() {
            // When
            BusinessRuleViolationException exception = assertThrows(
                    BusinessRuleViolationException.class,
                    () -> userService.getAllUsers(TEST_ADMIN_ID, null));

            // Then
            assertEquals("Pageable is required", exception.getMessage());
            verifyNoInteractions(userRepository);
        }

        @Test
        void shouldThrowException_whenNotAdmin() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);

            // When
            AccessDeniedException exception = assertThrows(
                    AccessDeniedException.class,
                    () -> userService.getAllUsers(TEST_ADMIN_ID, pageable));

            // Then
            assertTrue(exception.getMessage().contains("Permission to get all users denied"));
            verifyNoMoreInteractions(userRepository);
        }

    }

}
