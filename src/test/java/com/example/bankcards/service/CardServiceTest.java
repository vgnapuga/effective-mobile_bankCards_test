package com.example.bankcards.service;


import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
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

import com.example.bankcards.dto.card.request.CardCreateRequest;
import com.example.bankcards.exception.AccessDeniedException;
import com.example.bankcards.exception.BusinessRuleViolationException;
import com.example.bankcards.exception.DomainValidationException;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.model.card.Card;
import com.example.bankcards.model.card.CardStatus;
import com.example.bankcards.model.card.vo.CardBalance;
import com.example.bankcards.model.card.vo.CardExpiryDate;
import com.example.bankcards.model.card.vo.CardNumber;
import com.example.bankcards.model.role.Role;
import com.example.bankcards.model.role.RoleName;
import com.example.bankcards.model.user.User;
import com.example.bankcards.model.user.vo.Email;
import com.example.bankcards.model.user.vo.Password;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.security.CardEncryption;
import com.example.bankcards.service.contract.create.AdminCreateBehaviorTest;
import com.example.bankcards.service.contract.delete.AdminDeleteBehaviorTest;
import com.example.bankcards.service.contract.read.AdminReadBehaviorTest;
import com.example.bankcards.service.contract.read.ReadBehaviorTest;
import com.example.bankcards.service.contract.update.AdminUpdateBehaviorTest;


@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private CardEncryption cardEncryption;

    @Mock
    private UserService userService;

    @InjectMocks
    private CardService cardService;

    private static final Long TEST_ADMIN_ID = 1L;
    private static final Long TEST_USER_ID = 2L;
    private static final Long TEST_CARD_ID = 3L;
    private static final String TEST_CARD_NUMBER = "4532015112830366";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_HASHED_PASSWORD = "$2a$10$validBcryptHashWith60Characters1234567890123456781234";
    private static final String TEST_ENCRYPTED_CARD_NUMBER = "encryptedCardNumber123";

    // ---------- Helper methods ---------- //

    private User createTestUser() {
        Role userRole = new Role(RoleName.USER);
        return new User(new Email(TEST_EMAIL), new Password(TEST_HASHED_PASSWORD), userRole);
    }

    private Card createTestCard() {
        User owner = createTestUser();
        CardNumber cardNumber = new CardNumber(TEST_CARD_NUMBER);
        CardExpiryDate expiryDate = CardExpiryDate.of(2025, 12);
        CardBalance balance = new CardBalance(BigDecimal.valueOf(100.00));

        return Card.of(cardNumber, owner, expiryDate, CardStatus.ACTIVE, balance, cardEncryption);
    }

    private Card createExpiredCard() {
        User owner = createTestUser();
        CardNumber cardNumber = new CardNumber(TEST_CARD_NUMBER);
        CardExpiryDate expiryDate = CardExpiryDate.of(2020, 1);
        CardBalance balance = new CardBalance(BigDecimal.valueOf(100.00));

        Card card = Card.of(cardNumber, owner, expiryDate, CardStatus.EXPIRED, balance, cardEncryption);
        return card;
    }

    private void whenCheckAdminPermissionThrows() {
        doThrow(new AccessDeniedException("Permission denied")).when(userService).checkAdminPermissionTo(
                anyString(),
                anyLong());
    }

    private void whenFindUserById(User user) {
        when(userService.findUserById(anyLong())).thenReturn(user);
    }

    private void whenFindUserByIdThrows() {
        when(userService.findUserById(anyLong())).thenThrow(new ResourceNotFoundException("User not found"));
    }

    private void whenFindCardById(Optional<Card> optional) {
        when(cardRepository.findById(anyLong())).thenReturn(optional);
    }

    private void whenEncrypt() {
        when(cardEncryption.encrypt(any(CardNumber.class))).thenReturn(TEST_ENCRYPTED_CARD_NUMBER);
    }

    private void whenSave() {
        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    // ------------------------------------ //

    @Nested
    class CreateCardTests implements AdminCreateBehaviorTest {

        @Test
        @Override
        public void shouldReturnEntity_whenValidRequest() {
            User testUser = createTestUser();
            LocalDate futureDate = LocalDate.now().plusYears(2);

            whenFindUserById(testUser);
            whenEncrypt();
            whenSave();

            CardCreateRequest request = new CardCreateRequest(TEST_CARD_NUMBER, TEST_USER_ID, futureDate);

            Card result = cardService.createCard(TEST_ADMIN_ID, request);

            assertNotNull(result);
            assertEquals(testUser, result.getOwner());
            assertEquals(CardStatus.PENDING_ACTIVATION, result.getStatus());

            verify(userService).checkAdminPermissionTo("create card", TEST_ADMIN_ID);
            verify(userService).findUserById(TEST_USER_ID);
            verify(cardEncryption).encrypt(any(CardNumber.class));
            verify(cardRepository).save(any(Card.class));
            verifyNoMoreInteractions(userService, cardEncryption, cardRepository);
        }

        @Test
        @Override
        public void shouldThrowException_whenNullId() {
            LocalDate futureDate = LocalDate.now().plusYears(2);
            CardCreateRequest request = new CardCreateRequest(TEST_CARD_NUMBER, TEST_USER_ID, futureDate);

            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> cardService.createCard(null, request));

            assertEquals("Entity id is <null>", exception.getMessage());
            verifyNoInteractions(userService, cardEncryption, cardRepository);
        }

        @ParameterizedTest
        @ValueSource(longs = { 0L, -1L, -100L })
        @Override
        public void shouldThrowException_whenInvalidId(Long invalidId) {
            LocalDate futureDate = LocalDate.now().plusYears(2);
            CardCreateRequest request = new CardCreateRequest(TEST_CARD_NUMBER, TEST_USER_ID, futureDate);

            BusinessRuleViolationException exception = assertThrows(
                    BusinessRuleViolationException.class,
                    () -> cardService.createCard(invalidId, request));

            assertTrue(exception.getMessage().contains("Entity id must be positive"));
            verifyNoInteractions(userService, cardEncryption, cardRepository);
        }

        @Test
        @Override
        public void shouldThrowException_whenNotAdmin() {
            LocalDate futureDate = LocalDate.now().plusYears(2);
            whenCheckAdminPermissionThrows();

            CardCreateRequest request = new CardCreateRequest(TEST_CARD_NUMBER, TEST_USER_ID, futureDate);

            AccessDeniedException exception = assertThrows(
                    AccessDeniedException.class,
                    () -> cardService.createCard(TEST_ADMIN_ID, request));

            assertEquals("Permission denied", exception.getMessage());
            verify(userService).checkAdminPermissionTo("create card", TEST_ADMIN_ID);
            verifyNoMoreInteractions(userService);
            verifyNoInteractions(cardEncryption, cardRepository);
        }

        @Test
        void shouldThrowException_whenOwnerNotFound() {
            LocalDate futureDate = LocalDate.now().plusYears(2);

            whenFindUserByIdThrows();

            CardCreateRequest request = new CardCreateRequest(TEST_CARD_NUMBER, TEST_USER_ID, futureDate);

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> cardService.createCard(TEST_ADMIN_ID, request));

            assertEquals("User not found", exception.getMessage());
            verify(userService).checkAdminPermissionTo("create card", TEST_ADMIN_ID);
            verify(userService).findUserById(TEST_USER_ID);
            verifyNoMoreInteractions(userService);
            verifyNoInteractions(cardEncryption, cardRepository);
        }

        @Test
        void shouldThrowException_whenInvalidOwnerIdInRequest() {
            LocalDate futureDate = LocalDate.now().plusYears(2);
            CardCreateRequest request = new CardCreateRequest(TEST_CARD_NUMBER, null, futureDate);

            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> cardService.createCard(TEST_ADMIN_ID, request));

            assertEquals("Entity id is <null>", exception.getMessage());
            verifyNoInteractions(userService, cardEncryption, cardRepository);
        }
    }

    @Nested
    class GetCardByIdForAdminTests implements AdminReadBehaviorTest {

        @Test
        @Override
        public void shouldReturnEntity_whenValidRequest() {
            whenEncrypt();
            Card testCard = createTestCard();

            whenFindCardById(Optional.of(testCard));

            Card result = cardService.getCardByIdForAdmin(TEST_ADMIN_ID, TEST_CARD_ID);

            assertNotNull(result);
            assertEquals(testCard, result);

            verify(userService).checkAdminPermissionTo("get card", TEST_ADMIN_ID);
            verify(cardRepository).findById(TEST_CARD_ID);
            verifyNoMoreInteractions(userService, cardRepository);
        }

        @Test
        @Override
        public void shouldThrowException_whenNullRequesterId() {
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> cardService.getCardByIdForAdmin(null, TEST_CARD_ID));

            assertEquals("Entity id is <null>", exception.getMessage());
            verifyNoInteractions(userService, cardRepository);
        }

        @Test
        @Override
        public void shouldThrowException_whenNullIdToRead() {
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> cardService.getCardByIdForAdmin(TEST_ADMIN_ID, null));

            assertEquals("Entity id is <null>", exception.getMessage());
            verifyNoInteractions(userService, cardRepository);
        }

        @ParameterizedTest
        @ValueSource(longs = { 0L, -1L, -100L })
        @Override
        public void shouldThrowException_whenInvalidRequesterId(Long invalidId) {
            BusinessRuleViolationException exception = assertThrows(
                    BusinessRuleViolationException.class,
                    () -> cardService.getCardByIdForAdmin(invalidId, TEST_CARD_ID));

            assertTrue(exception.getMessage().contains("Entity id must be positive"));
            verifyNoInteractions(userService, cardRepository);
        }

        @ParameterizedTest
        @ValueSource(longs = { 0L, -1L, -100L })
        @Override
        public void shouldThrowException_whenInvalidIdToRead(Long invalidId) {
            BusinessRuleViolationException exception = assertThrows(
                    BusinessRuleViolationException.class,
                    () -> cardService.getCardByIdForAdmin(TEST_ADMIN_ID, invalidId));

            assertTrue(exception.getMessage().contains("Entity id must be positive"));
            verifyNoInteractions(userService, cardRepository);
        }

        @Test
        @Override
        public void shouldThrowException_whenNotAdmin() {
            whenCheckAdminPermissionThrows();

            AccessDeniedException exception = assertThrows(
                    AccessDeniedException.class,
                    () -> cardService.getCardByIdForAdmin(TEST_ADMIN_ID, TEST_CARD_ID));

            assertEquals("Permission denied", exception.getMessage());
            verify(userService).checkAdminPermissionTo("get card", TEST_ADMIN_ID);
            verifyNoMoreInteractions(userService);
            verifyNoInteractions(cardRepository);
        }

        @Test
        @Override
        public void shouldThrowException_whenEntityNotFound() {

            whenFindCardById(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> cardService.getCardByIdForAdmin(TEST_ADMIN_ID, TEST_CARD_ID));

            assertTrue(exception.getMessage().contains("was not found"));
            verify(userService).checkAdminPermissionTo("get card", TEST_ADMIN_ID);
            verify(cardRepository).findById(TEST_CARD_ID);
            verifyNoMoreInteractions(userService, cardRepository);
        }
    }

    @Nested
    class GetCardByIdForOwnerTests implements ReadBehaviorTest {

        @Test
        @Override
        public void shouldReturnEntity_whenValidRequest() {
            whenEncrypt();
            Card testCard = createTestCard();
            User testOwner = testCard.getOwner();

            whenFindUserById(testOwner);
            whenFindCardById(Optional.of(testCard));

            Card result = cardService.getCardByIdForOwner(TEST_USER_ID, TEST_CARD_ID);

            assertNotNull(result);
            assertEquals(testCard, result);

            verify(userService).findUserById(TEST_USER_ID);
            verify(cardRepository).findById(TEST_CARD_ID);
            verifyNoMoreInteractions(userService, cardRepository);
        }

        @Test
        @Override
        public void shouldThrowException_whenNullRequesterId() {
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> cardService.getCardByIdForOwner(null, TEST_CARD_ID));

            assertEquals("Entity id is <null>", exception.getMessage());
            verifyNoInteractions(userService, cardRepository);
        }

        @Test
        @Override
        public void shouldThrowException_whenNullIdToRead() {
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> cardService.getCardByIdForOwner(TEST_USER_ID, null));

            assertEquals("Entity id is <null>", exception.getMessage());
            verifyNoInteractions(userService, cardRepository);
        }

        @ParameterizedTest
        @ValueSource(longs = { 0L, -1L, -100L })
        @Override
        public void shouldThrowException_whenInvalidRequesterId(Long invalidId) {
            BusinessRuleViolationException exception = assertThrows(
                    BusinessRuleViolationException.class,
                    () -> cardService.getCardByIdForOwner(invalidId, TEST_CARD_ID));

            assertTrue(exception.getMessage().contains("Entity id must be positive"));
            verifyNoInteractions(userService, cardRepository);
        }

        @ParameterizedTest
        @ValueSource(longs = { 0L, -1L, -100L })
        @Override
        public void shouldThrowException_whenInvalidIdToRead(Long invalidId) {
            BusinessRuleViolationException exception = assertThrows(
                    BusinessRuleViolationException.class,
                    () -> cardService.getCardByIdForOwner(TEST_USER_ID, invalidId));

            assertTrue(exception.getMessage().contains("Entity id must be positive"));
            verifyNoInteractions(userService, cardRepository);
        }

        @Test
        @Override
        public void shouldThrowException_whenEntityNotFound() {
            User testOwner = createTestUser();
            whenFindUserById(testOwner);
            whenFindCardById(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> cardService.getCardByIdForOwner(TEST_USER_ID, TEST_CARD_ID));

            assertTrue(exception.getMessage().contains("was not found"));
            verify(userService).findUserById(TEST_USER_ID);
            verify(cardRepository).findById(TEST_CARD_ID);
            verifyNoMoreInteractions(userService, cardRepository);
        }

        @Test
        void shouldThrowException_whenNotCardOwner() {
            User differentUser = new User(
                    new Email("different@example.com"),
                    new Password(TEST_HASHED_PASSWORD),
                    new Role(RoleName.USER));

            whenEncrypt();
            Card testCard = createTestCard();
            whenFindUserById(differentUser);
            whenFindCardById(Optional.of(testCard));

            AccessDeniedException exception = assertThrows(
                    AccessDeniedException.class,
                    () -> cardService.getCardByIdForOwner(TEST_USER_ID, TEST_CARD_ID));

            assertTrue(exception.getMessage().contains("Permission to access card denied"));
            verify(userService).findUserById(TEST_USER_ID);
            verify(cardRepository).findById(TEST_CARD_ID);
            verifyNoMoreInteractions(userService, cardRepository);
        }
    }

    @Nested
    class DeleteCardByIdTests implements AdminDeleteBehaviorTest {

        @Test
        @Override
        public void shouldNotThrowException_whenValidRequest() {
            whenEncrypt();
            Card testCard = createTestCard();

            whenFindCardById(Optional.of(testCard));

            assertDoesNotThrow(() -> cardService.deleteCardById(TEST_ADMIN_ID, TEST_CARD_ID));

            verify(userService).checkAdminPermissionTo("delete card", TEST_ADMIN_ID);
            verify(cardRepository).findById(TEST_CARD_ID);
            verify(cardRepository).delete(testCard);
            verifyNoMoreInteractions(userService, cardRepository);
        }

        @Test
        @Override
        public void shouldThrowException_whenNullRequesterId() {
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> cardService.deleteCardById(null, TEST_CARD_ID));

            assertEquals("Entity id is <null>", exception.getMessage());
            verifyNoInteractions(userService, cardRepository);
        }

        @Test
        @Override
        public void shouldThrowException_whenNullIdToDelete() {
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> cardService.deleteCardById(TEST_ADMIN_ID, null));

            assertEquals("Entity id is <null>", exception.getMessage());
            verifyNoInteractions(userService, cardRepository);
        }

        @ParameterizedTest
        @ValueSource(longs = { 0L, -1L, -100L })
        @Override
        public void shouldThrowException_whenInvalidRequesterId(Long invalidId) {
            BusinessRuleViolationException exception = assertThrows(
                    BusinessRuleViolationException.class,
                    () -> cardService.deleteCardById(invalidId, TEST_CARD_ID));

            assertTrue(exception.getMessage().contains("Entity id must be positive"));
            verifyNoInteractions(userService, cardRepository);
        }

        @ParameterizedTest
        @ValueSource(longs = { 0L, -1L, -100L })
        @Override
        public void shouldThrowException_whenInvalidIdToDelete(Long invalidId) {
            BusinessRuleViolationException exception = assertThrows(
                    BusinessRuleViolationException.class,
                    () -> cardService.deleteCardById(TEST_ADMIN_ID, invalidId));

            assertTrue(exception.getMessage().contains("Entity id must be positive"));
            verifyNoInteractions(userService, cardRepository);
        }

        @Test
        @Override
        public void shouldThrowException_whenNotAdmin() {
            whenCheckAdminPermissionThrows();

            AccessDeniedException exception = assertThrows(
                    AccessDeniedException.class,
                    () -> cardService.deleteCardById(TEST_ADMIN_ID, TEST_CARD_ID));

            assertEquals("Permission denied", exception.getMessage());
            verify(userService).checkAdminPermissionTo("delete card", TEST_ADMIN_ID);
            verifyNoMoreInteractions(userService);
            verifyNoInteractions(cardRepository);
        }

        @Test
        @Override
        public void shouldThrowException_whenEntityNotFound() {

            whenFindCardById(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> cardService.deleteCardById(TEST_ADMIN_ID, TEST_CARD_ID));

            assertTrue(exception.getMessage().contains("was not found"));
            verify(userService).checkAdminPermissionTo("delete card", TEST_ADMIN_ID);
            verify(cardRepository).findById(TEST_CARD_ID);
            verifyNoMoreInteractions(userService, cardRepository);
        }
    }

    @Nested
    class ActivateCardByIdTests implements AdminUpdateBehaviorTest {

        @Test
        @Override
        public void shouldReturnEntity_whenValidRequest() {
            whenEncrypt();
            Card testCard = createTestCard();
            testCard.changeStatus(CardStatus.PENDING_ACTIVATION);

            whenFindCardById(Optional.of(testCard));
            whenSave();

            Card result = cardService.activateCardById(TEST_ADMIN_ID, TEST_CARD_ID);

            assertNotNull(result);
            assertEquals(CardStatus.ACTIVE, result.getStatus());

            verify(userService).checkAdminPermissionTo("activate card", TEST_ADMIN_ID);
            verify(cardRepository).findById(TEST_CARD_ID);
            verify(cardRepository).save(testCard);
            verifyNoMoreInteractions(userService, cardRepository);
        }

        @Test
        @Override
        public void shouldThrowException_whenNullRequesterId() {
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> cardService.activateCardById(null, TEST_CARD_ID));

            assertEquals("Entity id is <null>", exception.getMessage());
            verifyNoInteractions(userService, cardRepository);
        }

        @ParameterizedTest
        @ValueSource(longs = { 0L, -1L, -100L })
        @Override
        public void shouldThrowException_whenInvalidRequesterId(Long invalidId) {
            BusinessRuleViolationException exception = assertThrows(
                    BusinessRuleViolationException.class,
                    () -> cardService.activateCardById(invalidId, TEST_CARD_ID));

            assertTrue(exception.getMessage().contains("Entity id must be positive"));
            verifyNoInteractions(userService, cardRepository);
        }

        @Test
        @Override
        public void shouldThrowException_whenEntityNotFound() {

            whenFindCardById(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> cardService.activateCardById(TEST_ADMIN_ID, TEST_CARD_ID));

            assertTrue(exception.getMessage().contains("was not found"));
            verify(userService).checkAdminPermissionTo("activate card", TEST_ADMIN_ID);
            verify(cardRepository).findById(TEST_CARD_ID);
            verifyNoMoreInteractions(userService, cardRepository);
        }

        @Test
        @Override
        public void shouldThrowException_whenNotAdmin() {
            whenCheckAdminPermissionThrows();

            AccessDeniedException exception = assertThrows(
                    AccessDeniedException.class,
                    () -> cardService.activateCardById(TEST_ADMIN_ID, TEST_CARD_ID));

            assertEquals("Permission denied", exception.getMessage());
            verify(userService).checkAdminPermissionTo("activate card", TEST_ADMIN_ID);
            verifyNoMoreInteractions(userService);
            verifyNoInteractions(cardRepository);
        }

        @Test
        @Override
        public void shouldThrowException_whenNullIdToUpdate() {
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> cardService.activateCardById(TEST_ADMIN_ID, null));

            assertEquals("Entity id is <null>", exception.getMessage());
            verifyNoInteractions(userService, cardRepository);
        }

        @ParameterizedTest
        @ValueSource(longs = { 0L, -1L, -100L })
        @Override
        public void shouldThrowException_whenInvalidIdToUpdate(Long invalidId) {
            BusinessRuleViolationException exception = assertThrows(
                    BusinessRuleViolationException.class,
                    () -> cardService.activateCardById(TEST_ADMIN_ID, invalidId));

            assertTrue(exception.getMessage().contains("Entity id must be positive"));
            verifyNoInteractions(userService, cardRepository);
        }

        @Test
        void shouldThrowException_whenCardExpired() {
            whenEncrypt();
            Card expiredCard = createExpiredCard();

            whenFindCardById(Optional.of(expiredCard));

            BusinessRuleViolationException exception = assertThrows(
                    BusinessRuleViolationException.class,
                    () -> cardService.activateCardById(TEST_ADMIN_ID, TEST_CARD_ID));

            assertTrue(exception.getMessage().contains("EXPIRED"));
            verify(userService).checkAdminPermissionTo("activate card", TEST_ADMIN_ID);
            verify(cardRepository).findById(TEST_CARD_ID);
            verifyNoMoreInteractions(userService, cardRepository);
        }

        @Test
        void shouldThrowException_whenCardAlreadyActive() {
            whenEncrypt();
            Card activeCard = createTestCard();

            whenFindCardById(Optional.of(activeCard));

            BusinessRuleViolationException exception = assertThrows(
                    BusinessRuleViolationException.class,
                    () -> cardService.activateCardById(TEST_ADMIN_ID, TEST_CARD_ID));

            assertTrue(exception.getMessage().contains("already ACTIVE"));
            verify(userService).checkAdminPermissionTo("activate card", TEST_ADMIN_ID);
            verify(cardRepository).findById(TEST_CARD_ID);
            verifyNoMoreInteractions(userService, cardRepository);
        }
    }

    @Nested
    class BlockCardByIdTests implements AdminUpdateBehaviorTest {

        @Test
        @Override
        public void shouldReturnEntity_whenValidRequest() {
            whenEncrypt();
            Card testCard = createTestCard();

            whenFindCardById(Optional.of(testCard));
            whenSave();

            Card result = cardService.blockCardById(TEST_ADMIN_ID, TEST_CARD_ID);

            assertNotNull(result);
            assertEquals(CardStatus.BLOCKED, result.getStatus());

            verify(userService).checkAdminPermissionTo("block card", TEST_ADMIN_ID);
            verify(cardRepository).findById(TEST_CARD_ID);
            verify(cardRepository).save(testCard);
            verifyNoMoreInteractions(userService, cardRepository);
        }

        @Test
        @Override
        public void shouldThrowException_whenNullRequesterId() {
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> cardService.blockCardById(null, TEST_CARD_ID));

            assertEquals("Entity id is <null>", exception.getMessage());
            verifyNoInteractions(userService, cardRepository);
        }

        @ParameterizedTest
        @ValueSource(longs = { 0L, -1L, -100L })
        @Override
        public void shouldThrowException_whenInvalidRequesterId(Long invalidId) {
            BusinessRuleViolationException exception = assertThrows(
                    BusinessRuleViolationException.class,
                    () -> cardService.blockCardById(invalidId, TEST_CARD_ID));

            assertTrue(exception.getMessage().contains("Entity id must be positive"));
            verifyNoInteractions(userService, cardRepository);
        }

        @Test
        @Override
        public void shouldThrowException_whenEntityNotFound() {

            whenFindCardById(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> cardService.blockCardById(TEST_ADMIN_ID, TEST_CARD_ID));

            assertTrue(exception.getMessage().contains("was not found"));
            verify(userService).checkAdminPermissionTo("block card", TEST_ADMIN_ID);
            verify(cardRepository).findById(TEST_CARD_ID);
            verifyNoMoreInteractions(userService, cardRepository);
        }

        @Test
        @Override
        public void shouldThrowException_whenNotAdmin() {
            whenCheckAdminPermissionThrows();

            AccessDeniedException exception = assertThrows(
                    AccessDeniedException.class,
                    () -> cardService.blockCardById(TEST_ADMIN_ID, TEST_CARD_ID));

            assertEquals("Permission denied", exception.getMessage());
            verify(userService).checkAdminPermissionTo("block card", TEST_ADMIN_ID);
            verifyNoMoreInteractions(userService);
            verifyNoInteractions(cardRepository);
        }

        @Test
        @Override
        public void shouldThrowException_whenNullIdToUpdate() {
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> cardService.blockCardById(TEST_ADMIN_ID, null));

            assertEquals("Entity id is <null>", exception.getMessage());
            verifyNoInteractions(userService, cardRepository);
        }

        @ParameterizedTest
        @ValueSource(longs = { 0L, -1L, -100L })
        @Override
        public void shouldThrowException_whenInvalidIdToUpdate(Long invalidId) {
            BusinessRuleViolationException exception = assertThrows(
                    BusinessRuleViolationException.class,
                    () -> cardService.blockCardById(TEST_ADMIN_ID, invalidId));

            assertTrue(exception.getMessage().contains("Entity id must be positive"));
            verifyNoInteractions(userService, cardRepository);
        }

        @Test
        void shouldThrowException_whenCardExpired() {
            whenEncrypt();
            Card expiredCard = createExpiredCard();

            whenFindCardById(Optional.of(expiredCard));

            BusinessRuleViolationException exception = assertThrows(
                    BusinessRuleViolationException.class,
                    () -> cardService.blockCardById(TEST_ADMIN_ID, TEST_CARD_ID));

            assertTrue(exception.getMessage().contains("EXPIRED"));
            verify(userService).checkAdminPermissionTo("block card", TEST_ADMIN_ID);
            verify(cardRepository).findById(TEST_CARD_ID);
            verifyNoMoreInteractions(userService, cardRepository);
        }

        @Test
        void shouldThrowException_whenCardAlreadyBlocked() {
            whenEncrypt();
            Card blockedCard = createTestCard();
            blockedCard.changeStatus(CardStatus.BLOCKED);

            whenFindCardById(Optional.of(blockedCard));

            BusinessRuleViolationException exception = assertThrows(
                    BusinessRuleViolationException.class,
                    () -> cardService.blockCardById(TEST_ADMIN_ID, TEST_CARD_ID));

            assertTrue(exception.getMessage().contains("already BLOCKED"));
            verify(userService).checkAdminPermissionTo("block card", TEST_ADMIN_ID);
            verify(cardRepository).findById(TEST_CARD_ID);
            verifyNoMoreInteractions(userService, cardRepository);
        }
    }

    @Nested
    class GetAllCardsForAdminTests {

        @Test
        void shouldReturnCardPage_whenValidRequest() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Card> cardPage = new PageImpl<>(new ArrayList<>());

            when(cardRepository.findAll(pageable)).thenReturn(cardPage);

            Page<Card> result = cardService.getAllCardsForAdmin(TEST_ADMIN_ID, pageable);

            assertNotNull(result);
            verify(userService).checkAdminPermissionTo("get all cards", TEST_ADMIN_ID);
            verify(cardRepository).findAll(pageable);
            verifyNoMoreInteractions(userService, cardRepository);
        }

        @Test
        void shouldThrowException_whenNullPageable() {
            BusinessRuleViolationException exception = assertThrows(
                    BusinessRuleViolationException.class,
                    () -> cardService.getAllCardsForAdmin(TEST_ADMIN_ID, null));

            assertEquals("Pageable is required", exception.getMessage());
            verifyNoInteractions(userService, cardRepository);
        }

        @Test
        void shouldThrowException_whenNotAdmin() {
            Pageable pageable = PageRequest.of(0, 10);
            whenCheckAdminPermissionThrows();

            AccessDeniedException exception = assertThrows(
                    AccessDeniedException.class,
                    () -> cardService.getAllCardsForAdmin(TEST_ADMIN_ID, pageable));

            assertEquals("Permission denied", exception.getMessage());
            verify(userService).checkAdminPermissionTo("get all cards", TEST_ADMIN_ID);
            verifyNoMoreInteractions(userService);
            verifyNoInteractions(cardRepository);
        }
    }

    @Nested
    class GetAllCardsForOwnerTests {

        @Test
        void shouldReturnCardPage_whenValidRequest() {
            User testOwner = createTestUser();
            Pageable pageable = PageRequest.of(0, 10);
            Page<Card> cardPage = new PageImpl<>(new ArrayList<>());

            whenFindUserById(testOwner);
            when(cardRepository.findAllByOwner(testOwner, pageable)).thenReturn(cardPage);

            Page<Card> result = cardService.getAllCardsForOwner(TEST_USER_ID, pageable);

            assertNotNull(result);
            verify(userService).findUserById(TEST_USER_ID);
            verify(cardRepository).findAllByOwner(testOwner, pageable);
            verifyNoMoreInteractions(userService, cardRepository);
        }

        @Test
        void shouldThrowException_whenNullPageable() {
            BusinessRuleViolationException exception = assertThrows(
                    BusinessRuleViolationException.class,
                    () -> cardService.getAllCardsForOwner(TEST_USER_ID, null));

            assertEquals("Pageable is required", exception.getMessage());
            verifyNoInteractions(userService, cardRepository);
        }

        @Test
        void shouldThrowException_whenOwnerNotFound() {
            Pageable pageable = PageRequest.of(0, 10);
            whenFindUserByIdThrows();

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> cardService.getAllCardsForOwner(TEST_USER_ID, pageable));

            assertEquals("User not found", exception.getMessage());
            verify(userService).findUserById(TEST_USER_ID);
            verifyNoMoreInteractions(userService);
            verifyNoInteractions(cardRepository);
        }
    }
}