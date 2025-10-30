package com.example.bankcards.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.math.BigDecimal;
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

import com.example.bankcards.dto.transfer.request.TransferRequest;
import com.example.bankcards.exception.AccessDeniedException;
import com.example.bankcards.exception.BusinessRuleViolationException;
import com.example.bankcards.exception.DomainValidationException;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.model.BaseEntity;
import com.example.bankcards.model.card.Card;
import com.example.bankcards.model.card.CardStatus;
import com.example.bankcards.model.card.vo.CardBalance;
import com.example.bankcards.model.card.vo.CardExpiryDate;
import com.example.bankcards.model.card.vo.CardNumber;
import com.example.bankcards.model.transfer.Transfer;
import com.example.bankcards.model.user.Role;
import com.example.bankcards.model.user.User;
import com.example.bankcards.model.user.vo.Email;
import com.example.bankcards.model.user.vo.Password;
import com.example.bankcards.repository.TransferRepository;
import com.example.bankcards.security.CardEncryption;
import com.example.bankcards.service.contract.create.CreateBehaviorTest;
import com.example.bankcards.service.contract.read.AdminReadBehaviorTest;
import com.example.bankcards.service.contract.read.ReadBehaviorTest;


@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @Mock
    private TransferRepository transferRepository;

    @Mock
    private UserService userService;

    @Mock
    private CardService cardService;

    @Mock
    private CardEncryption cardEncryption;

    @InjectMocks
    private TransferService transferService;

    private static final Long TEST_ADMIN_ID = 1L;
    private static final Long TEST_USER_ID = 2L;
    private static final Long TEST_TRANSFER_ID = 3L;
    private static final Long TEST_FROM_CARD_ID = 4L;
    private static final Long TEST_TO_CARD_ID = 5L;
    private static final String TEST_CARD_NUMBER = "4532015112830366";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_HASHED_PASSWORD = "$2a$10$validBcryptHashWith60Characters1234567890123456781234";
    private static final String TEST_ENCRYPTED_CARD_NUMBER = "encryptedCardNumber123";
    private static final BigDecimal TEST_TRANSFER_AMOUNT = BigDecimal.valueOf(100.00);

    // ---------- Helper methods ---------- //

    private void setId(Object entity, Long id) {
        try {
            Field idField = BaseEntity.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(entity, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private User createTestUser() {
        return new User(new Email(TEST_EMAIL), new Password(TEST_HASHED_PASSWORD), Role.USER);
    }

    private Card createTestCard(User owner, BigDecimal balance, CardStatus status) {
        CardNumber cardNumber = new CardNumber(TEST_CARD_NUMBER);
        CardExpiryDate expiryDate = CardExpiryDate.of(2025, 12);
        CardBalance cardBalance = new CardBalance(balance);

        return Card.of(cardNumber, owner, expiryDate, status, cardBalance, cardEncryption);
    }

    private Transfer createTestTransfer(User owner, Card fromCard, Card toCard) {
        return Transfer.of(
                owner,
                fromCard,
                toCard,
                new com.example.bankcards.model.transfer.vo.Amount(TEST_TRANSFER_AMOUNT));
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

    private void whenFindTransferById(Optional<Transfer> optional) {
        when(transferRepository.findById(anyLong())).thenReturn(optional);
    }

    private void whenEncrypt() {
        when(cardEncryption.encrypt(any(CardNumber.class))).thenReturn(TEST_ENCRYPTED_CARD_NUMBER);
    }

    private void whenSave() {
        when(transferRepository.save(any(Transfer.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    // ------------------------------------ //

    @Nested
    class TransferBetweenOwnCardsTests implements CreateBehaviorTest {

        @Test
        @Override
        public void shouldReturnEntity_whenValidRequest() {
            User testUser = createTestUser();
            setId(testUser, TEST_USER_ID);
            whenEncrypt();
            Card fromCard = createTestCard(testUser, BigDecimal.valueOf(500.00), CardStatus.ACTIVE);
            Card toCard = createTestCard(testUser, BigDecimal.valueOf(100.00), CardStatus.ACTIVE);

            whenFindUserById(testUser);
            when(cardService.findCardByIdForOwner(TEST_FROM_CARD_ID, testUser)).thenReturn(fromCard);
            when(cardService.findCardByIdForOwner(TEST_TO_CARD_ID, testUser)).thenReturn(toCard);
            whenSave();

            TransferRequest request = new TransferRequest(TEST_FROM_CARD_ID, TEST_TO_CARD_ID, TEST_TRANSFER_AMOUNT);

            Transfer result = transferService.transferBetweenOwnCards(TEST_USER_ID, request);

            assertNotNull(result);
            assertEquals(testUser, result.getOwner());
            assertEquals(fromCard, result.getFromCard());
            assertEquals(toCard, result.getToCard());
            assertEquals(TEST_TRANSFER_AMOUNT, result.getAmount().getValue());

            verify(userService).findUserById(TEST_USER_ID);
            verify(cardService).findCardByIdForOwner(TEST_FROM_CARD_ID, testUser);
            verify(cardService).findCardByIdForOwner(TEST_TO_CARD_ID, testUser);
            verify(transferRepository).save(any(Transfer.class));
            verifyNoMoreInteractions(userService, cardService, transferRepository);
        }

        @Test
        @Override
        public void shouldThrowException_whenNullId() {
            TransferRequest request = new TransferRequest(TEST_FROM_CARD_ID, TEST_TO_CARD_ID, TEST_TRANSFER_AMOUNT);

            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> transferService.transferBetweenOwnCards(null, request));

            assertEquals("Entity id is <null>", exception.getMessage());
            verifyNoInteractions(userService, cardService, transferRepository);
        }

        @ParameterizedTest
        @ValueSource(longs = { 0L, -1L, -100L })
        @Override
        public void shouldThrowException_whenInvalidId(Long invalidId) {
            TransferRequest request = new TransferRequest(TEST_FROM_CARD_ID, TEST_TO_CARD_ID, TEST_TRANSFER_AMOUNT);

            BusinessRuleViolationException exception = assertThrows(
                    BusinessRuleViolationException.class,
                    () -> transferService.transferBetweenOwnCards(invalidId, request));

            assertTrue(exception.getMessage().contains("Entity id must be positive"));
            verifyNoInteractions(userService, cardService, transferRepository);
        }

        @Test
        void shouldThrowException_whenUserNotFound() {
            whenFindUserByIdThrows();

            TransferRequest request = new TransferRequest(TEST_FROM_CARD_ID, TEST_TO_CARD_ID, TEST_TRANSFER_AMOUNT);

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> transferService.transferBetweenOwnCards(TEST_USER_ID, request));

            assertEquals("User not found", exception.getMessage());
            verify(userService).findUserById(TEST_USER_ID);
            verifyNoMoreInteractions(userService);
            verifyNoInteractions(cardService, transferRepository);
        }

        @Test
        void shouldThrowException_whenSameCard() {
            User testUser = createTestUser();
            whenEncrypt();
            Card sameCard = createTestCard(testUser, BigDecimal.valueOf(500.00), CardStatus.ACTIVE);

            whenFindUserById(testUser);
            when(cardService.findCardByIdForOwner(anyLong(), any(User.class))).thenReturn(sameCard);

            TransferRequest request = new TransferRequest(TEST_FROM_CARD_ID, TEST_FROM_CARD_ID, TEST_TRANSFER_AMOUNT);

            BusinessRuleViolationException exception = assertThrows(
                    BusinessRuleViolationException.class,
                    () -> transferService.transferBetweenOwnCards(TEST_USER_ID, request));

            assertEquals("Cannot transfer to the same card", exception.getMessage());
            verify(userService).findUserById(TEST_USER_ID);
            verify(cardService, times(2)).findCardByIdForOwner(TEST_FROM_CARD_ID, testUser);
            verifyNoMoreInteractions(userService, cardService);
            verifyNoInteractions(transferRepository);
        }

        @Test
        void shouldThrowException_whenFromCardNotActive() {
            User testUser = createTestUser();
            whenEncrypt();
            Card fromCard = createTestCard(testUser, BigDecimal.valueOf(500.00), CardStatus.BLOCKED);
            Card toCard = createTestCard(testUser, BigDecimal.valueOf(100.00), CardStatus.ACTIVE);

            whenFindUserById(testUser);
            when(cardService.findCardByIdForOwner(TEST_FROM_CARD_ID, testUser)).thenReturn(fromCard);
            when(cardService.findCardByIdForOwner(TEST_TO_CARD_ID, testUser)).thenReturn(toCard);

            TransferRequest request = new TransferRequest(TEST_FROM_CARD_ID, TEST_TO_CARD_ID, TEST_TRANSFER_AMOUNT);

            BusinessRuleViolationException exception = assertThrows(
                    BusinessRuleViolationException.class,
                    () -> transferService.transferBetweenOwnCards(TEST_USER_ID, request));

            assertEquals("Both cards for transfer must be ACTIVE", exception.getMessage());
            verify(userService).findUserById(TEST_USER_ID);
            verify(cardService).findCardByIdForOwner(TEST_FROM_CARD_ID, testUser);
            verify(cardService).findCardByIdForOwner(TEST_TO_CARD_ID, testUser);
            verifyNoMoreInteractions(userService, cardService);
            verifyNoInteractions(transferRepository);
        }

        @Test
        void shouldThrowException_whenToCardNotActive() {
            User testUser = createTestUser();
            whenEncrypt();
            Card fromCard = createTestCard(testUser, BigDecimal.valueOf(500.00), CardStatus.ACTIVE);
            Card toCard = createTestCard(testUser, BigDecimal.valueOf(100.00), CardStatus.PENDING_ACTIVATION);

            whenFindUserById(testUser);
            when(cardService.findCardByIdForOwner(TEST_FROM_CARD_ID, testUser)).thenReturn(fromCard);
            when(cardService.findCardByIdForOwner(TEST_TO_CARD_ID, testUser)).thenReturn(toCard);

            TransferRequest request = new TransferRequest(TEST_FROM_CARD_ID, TEST_TO_CARD_ID, TEST_TRANSFER_AMOUNT);

            BusinessRuleViolationException exception = assertThrows(
                    BusinessRuleViolationException.class,
                    () -> transferService.transferBetweenOwnCards(TEST_USER_ID, request));

            assertEquals("Both cards for transfer must be ACTIVE", exception.getMessage());
            verify(userService).findUserById(TEST_USER_ID);
            verify(cardService).findCardByIdForOwner(TEST_FROM_CARD_ID, testUser);
            verify(cardService).findCardByIdForOwner(TEST_TO_CARD_ID, testUser);
            verifyNoMoreInteractions(userService, cardService);
            verifyNoInteractions(transferRepository);
        }

        @Test
        void shouldThrowException_whenInsufficientFunds() {
            User testUser = createTestUser();
            whenEncrypt();
            Card fromCard = createTestCard(testUser, BigDecimal.valueOf(50.00), CardStatus.ACTIVE);
            Card toCard = createTestCard(testUser, BigDecimal.valueOf(100.00), CardStatus.ACTIVE);

            whenFindUserById(testUser);
            when(cardService.findCardByIdForOwner(TEST_FROM_CARD_ID, testUser)).thenReturn(fromCard);
            when(cardService.findCardByIdForOwner(TEST_TO_CARD_ID, testUser)).thenReturn(toCard);

            TransferRequest request = new TransferRequest(TEST_FROM_CARD_ID, TEST_TO_CARD_ID, TEST_TRANSFER_AMOUNT);

            BusinessRuleViolationException exception = assertThrows(
                    BusinessRuleViolationException.class,
                    () -> transferService.transferBetweenOwnCards(TEST_USER_ID, request));

            assertEquals("Insufficient funds", exception.getMessage());
            verify(userService).findUserById(TEST_USER_ID);
            verify(cardService).findCardByIdForOwner(TEST_FROM_CARD_ID, testUser);
            verify(cardService).findCardByIdForOwner(TEST_TO_CARD_ID, testUser);
            verifyNoMoreInteractions(userService, cardService);
            verifyNoInteractions(transferRepository);
        }

        @Test
        void shouldThrowException_whenNullFromCardId() {
            TransferRequest request = new TransferRequest(null, TEST_TO_CARD_ID, TEST_TRANSFER_AMOUNT);

            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> transferService.transferBetweenOwnCards(TEST_USER_ID, request));

            assertEquals("Entity id is <null>", exception.getMessage());
            verifyNoInteractions(userService, cardService, transferRepository);
        }

        @Test
        void shouldThrowException_whenNullToCardId() {
            TransferRequest request = new TransferRequest(TEST_FROM_CARD_ID, null, TEST_TRANSFER_AMOUNT);

            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> transferService.transferBetweenOwnCards(TEST_USER_ID, request));

            assertEquals("Entity id is <null>", exception.getMessage());
            verifyNoInteractions(userService, cardService, transferRepository);
        }
    }

    @Nested
    class GetTransferByIdForAdminTests implements AdminReadBehaviorTest {

        @Test
        @Override
        public void shouldReturnEntity_whenValidRequest() {
            User testUser = createTestUser();
            setId(testUser, TEST_ADMIN_ID);
            whenEncrypt();
            Card fromCard = createTestCard(testUser, BigDecimal.valueOf(500.00), CardStatus.ACTIVE);
            Card toCard = createTestCard(testUser, BigDecimal.valueOf(100.00), CardStatus.ACTIVE);
            Transfer testTransfer = createTestTransfer(testUser, fromCard, toCard);

            whenFindTransferById(Optional.of(testTransfer));

            Transfer result = transferService.getTransferByIdForAdmin(TEST_ADMIN_ID, TEST_TRANSFER_ID);

            assertNotNull(result);
            assertEquals(testTransfer, result);

            verify(userService).checkAdminPermissionTo("get transfer", TEST_ADMIN_ID);
            verify(transferRepository).findById(TEST_TRANSFER_ID);
            verifyNoMoreInteractions(userService, transferRepository);
        }

        @Test
        @Override
        public void shouldThrowException_whenNullRequesterId() {
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> transferService.getTransferByIdForAdmin(null, TEST_TRANSFER_ID));

            assertEquals("Entity id is <null>", exception.getMessage());
            verifyNoInteractions(userService, transferRepository);
        }

        @Test
        @Override
        public void shouldThrowException_whenNullIdToRead() {
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> transferService.getTransferByIdForAdmin(TEST_ADMIN_ID, null));

            assertEquals("Entity id is <null>", exception.getMessage());
            verifyNoInteractions(userService, transferRepository);
        }

        @ParameterizedTest
        @ValueSource(longs = { 0L, -1L, -100L })
        @Override
        public void shouldThrowException_whenInvalidRequesterId(Long invalidId) {
            BusinessRuleViolationException exception = assertThrows(
                    BusinessRuleViolationException.class,
                    () -> transferService.getTransferByIdForAdmin(invalidId, TEST_TRANSFER_ID));

            assertTrue(exception.getMessage().contains("Entity id must be positive"));
            verifyNoInteractions(userService, transferRepository);
        }

        @ParameterizedTest
        @ValueSource(longs = { 0L, -1L, -100L })
        @Override
        public void shouldThrowException_whenInvalidIdToRead(Long invalidId) {
            BusinessRuleViolationException exception = assertThrows(
                    BusinessRuleViolationException.class,
                    () -> transferService.getTransferByIdForAdmin(TEST_ADMIN_ID, invalidId));

            assertTrue(exception.getMessage().contains("Entity id must be positive"));
            verifyNoInteractions(userService, transferRepository);
        }

        @Test
        @Override
        public void shouldThrowException_whenNotAdmin() {
            whenCheckAdminPermissionThrows();

            AccessDeniedException exception = assertThrows(
                    AccessDeniedException.class,
                    () -> transferService.getTransferByIdForAdmin(TEST_ADMIN_ID, TEST_TRANSFER_ID));

            assertEquals("Permission denied", exception.getMessage());
            verify(userService).checkAdminPermissionTo("get transfer", TEST_ADMIN_ID);
            verifyNoMoreInteractions(userService);
            verifyNoInteractions(transferRepository);
        }

        @Test
        @Override
        public void shouldThrowException_whenEntityNotFound() {
            whenFindTransferById(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> transferService.getTransferByIdForAdmin(TEST_ADMIN_ID, TEST_TRANSFER_ID));

            assertTrue(exception.getMessage().contains("not found"));
            verify(userService).checkAdminPermissionTo("get transfer", TEST_ADMIN_ID);
            verify(transferRepository).findById(TEST_TRANSFER_ID);
            verifyNoMoreInteractions(userService, transferRepository);
        }

    }

    @Nested
    class GetTransferByIdForOwnerTests implements ReadBehaviorTest {

        @Test
        @Override
        public void shouldReturnEntity_whenValidRequest() {
            User testUser = createTestUser();
            setId(testUser, TEST_USER_ID);
            whenEncrypt();
            Card fromCard = createTestCard(testUser, BigDecimal.valueOf(500.00), CardStatus.ACTIVE);
            Card toCard = createTestCard(testUser, BigDecimal.valueOf(100.00), CardStatus.ACTIVE);
            Transfer testTransfer = createTestTransfer(testUser, fromCard, toCard);

            whenFindUserById(testUser);
            whenFindTransferById(Optional.of(testTransfer));

            Transfer result = transferService.getTransferByIdForOwner(TEST_USER_ID, TEST_TRANSFER_ID);

            assertNotNull(result);
            assertEquals(testTransfer, result);

            verify(userService).findUserById(TEST_USER_ID);
            verify(transferRepository).findById(TEST_TRANSFER_ID);
            verifyNoMoreInteractions(userService, transferRepository);
        }

        @Test
        @Override
        public void shouldThrowException_whenNullRequesterId() {
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> transferService.getTransferByIdForOwner(null, TEST_TRANSFER_ID));

            assertEquals("Entity id is <null>", exception.getMessage());
            verifyNoInteractions(userService, transferRepository);
        }

        @Test
        @Override
        public void shouldThrowException_whenNullIdToRead() {
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> transferService.getTransferByIdForOwner(TEST_USER_ID, null));

            assertEquals("Entity id is <null>", exception.getMessage());
            verifyNoInteractions(userService, transferRepository);
        }

        @ParameterizedTest
        @ValueSource(longs = { 0L, -1L, -100L })
        @Override
        public void shouldThrowException_whenInvalidRequesterId(Long invalidId) {
            BusinessRuleViolationException exception = assertThrows(
                    BusinessRuleViolationException.class,
                    () -> transferService.getTransferByIdForOwner(invalidId, TEST_TRANSFER_ID));

            assertTrue(exception.getMessage().contains("Entity id must be positive"));
            verifyNoInteractions(userService, transferRepository);
        }

        @ParameterizedTest
        @ValueSource(longs = { 0L, -1L, -100L })
        @Override
        public void shouldThrowException_whenInvalidIdToRead(Long invalidId) {
            BusinessRuleViolationException exception = assertThrows(
                    BusinessRuleViolationException.class,
                    () -> transferService.getTransferByIdForOwner(TEST_USER_ID, invalidId));

            assertTrue(exception.getMessage().contains("Entity id must be positive"));
            verifyNoInteractions(userService, transferRepository);
        }

        @Test
        @Override
        public void shouldThrowException_whenEntityNotFound() {
            User testUser = createTestUser();
            whenFindUserById(testUser);
            whenFindTransferById(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> transferService.getTransferByIdForOwner(TEST_USER_ID, TEST_TRANSFER_ID));

            assertTrue(exception.getMessage().contains("was not found"));
            verify(userService).findUserById(TEST_USER_ID);
            verify(transferRepository).findById(TEST_TRANSFER_ID);
            verifyNoMoreInteractions(userService, transferRepository);
        }

        @Test
        void shouldThrowException_whenNotTransferOwner() {
            User requestUser = createTestUser();
            setId(requestUser, 10L);
            User differentUser = new User(
                    new Email("different@example.com"),
                    new Password(TEST_HASHED_PASSWORD),
                    Role.USER);
            setId(differentUser, TEST_USER_ID);
            whenEncrypt();
            Card fromCard = createTestCard(requestUser, BigDecimal.valueOf(500.00), CardStatus.ACTIVE);
            Card toCard = createTestCard(requestUser, BigDecimal.valueOf(100.00), CardStatus.ACTIVE);
            Transfer testTransfer = createTestTransfer(requestUser, fromCard, toCard);

            whenFindUserById(differentUser);
            whenFindTransferById(Optional.of(testTransfer));

            AccessDeniedException exception = assertThrows(
                    AccessDeniedException.class,
                    () -> transferService.getTransferByIdForOwner(TEST_USER_ID, TEST_TRANSFER_ID));

            assertTrue(exception.getMessage().contains("Permission to access card denied"));
            verify(userService).findUserById(TEST_USER_ID);
            verify(transferRepository).findById(TEST_TRANSFER_ID);
            verifyNoMoreInteractions(userService, transferRepository);
        }

    }

    @Nested
    class GetAllTransfersForAdminTests {

        @Test
        void shouldReturnTransferPage_whenValidRequest() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Transfer> transferPage = new PageImpl<>(new ArrayList<>());

            when(transferRepository.findAll(pageable)).thenReturn(transferPage);

            Page<Transfer> result = transferService.getAllTransfersForAdmin(TEST_ADMIN_ID, pageable);

            assertNotNull(result);
            verify(userService).checkAdminPermissionTo("get all transfers", TEST_ADMIN_ID);
            verify(transferRepository).findAll(pageable);
            verifyNoMoreInteractions(userService, transferRepository);
        }

        @Test
        void shouldThrowException_whenNullPageable() {
            BusinessRuleViolationException exception = assertThrows(
                    BusinessRuleViolationException.class,
                    () -> transferService.getAllTransfersForAdmin(TEST_ADMIN_ID, null));

            assertEquals("Pageable is required", exception.getMessage());
            verifyNoInteractions(userService, transferRepository);
        }

        @Test
        void shouldThrowException_whenNotAdmin() {
            Pageable pageable = PageRequest.of(0, 10);
            whenCheckAdminPermissionThrows();

            AccessDeniedException exception = assertThrows(
                    AccessDeniedException.class,
                    () -> transferService.getAllTransfersForAdmin(TEST_ADMIN_ID, pageable));

            assertEquals("Permission denied", exception.getMessage());
            verify(userService).checkAdminPermissionTo("get all transfers", TEST_ADMIN_ID);
            verifyNoMoreInteractions(userService);
            verifyNoInteractions(transferRepository);
        }
    }

    @Nested
    class GetAllTransfersForOwnerTests {

        @Test
        void shouldReturnTransferPage_whenValidRequest() {
            User testUser = createTestUser();
            Pageable pageable = PageRequest.of(0, 10);
            Page<Transfer> transferPage = new PageImpl<>(new ArrayList<>());

            whenFindUserById(testUser);
            when(transferRepository.findAllByOwner(testUser, pageable)).thenReturn(transferPage);

            Page<Transfer> result = transferService.getAllTransfersForOwner(TEST_USER_ID, pageable);

            assertNotNull(result);
            verify(userService).findUserById(TEST_USER_ID);
            verify(transferRepository).findAllByOwner(testUser, pageable);
            verifyNoMoreInteractions(userService, transferRepository);
        }

        @Test
        void shouldThrowException_whenNullPageable() {
            BusinessRuleViolationException exception = assertThrows(
                    BusinessRuleViolationException.class,
                    () -> transferService.getAllTransfersForOwner(TEST_USER_ID, null));

            assertEquals("Pageable is required", exception.getMessage());
            verifyNoInteractions(userService, transferRepository);
        }

        @Test
        void shouldThrowException_whenOwnerNotFound() {
            Pageable pageable = PageRequest.of(0, 10);
            whenFindUserByIdThrows();

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> transferService.getAllTransfersForOwner(TEST_USER_ID, pageable));

            assertEquals("User not found", exception.getMessage());
            verify(userService).findUserById(TEST_USER_ID);
            verifyNoMoreInteractions(userService);
            verifyNoInteractions(transferRepository);
        }

    }

}
