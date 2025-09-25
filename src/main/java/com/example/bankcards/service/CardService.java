package com.example.bankcards.service;


import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.bankcards.dto.card.request.CardCreateRequest;
import com.example.bankcards.exception.AccessDeniedException;
import com.example.bankcards.exception.BusinessRuleViolationException;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.model.card.Card;
import com.example.bankcards.model.card.CardStatus;
import com.example.bankcards.model.card.vo.CardBalance;
import com.example.bankcards.model.card.vo.CardExpiryDate;
import com.example.bankcards.model.card.vo.CardNumber;
import com.example.bankcards.model.user.User;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.security.CardEncryption;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
@RequiredArgsConstructor
public class CardService extends BaseService {

    private final CardRepository cardRepository;
    private final CardEncryption cardEncryption;
    private final UserService userService;

    // ---------- Helper methods ---------- //

    private final Card findCardByIdForAdmin(final Long cardId) {
        return cardRepository.findById(cardId).orElseThrow(
                () -> new ResourceNotFoundException(String.format("Card with id=%d was not found", cardId)));
    }

    private final void checkExpiryDate(final Card card) {
        if (card.isExpired())
            throw new BusinessRuleViolationException(String.format("Card with id=%d EXPIRED", card.getId()));
    }

    public final Card findCardByIdForOwner(final Long cardId, final User owner) {
        Card card = cardRepository.findById(cardId).orElseThrow(
                () -> new ResourceNotFoundException(String.format("Card with id=%d was not found", cardId)));

        if (!card.getOwner().equals(owner))
            throw new AccessDeniedException(String.format("Permission to access card denied for id=%d", owner.getId()));

        return card;
    }

    // ------------------------------------ //

    @Transactional
    public final Card createCard(final Long adminId, final CardCreateRequest request) {
        validateId(adminId);

        Long forUserId = request.ownerId();
        validateId(forUserId);

        userService.checkAdminPermissionTo("create card", adminId);
        User user = userService.findUserById(forUserId);

        CardNumber cardNumber = new CardNumber(request.cardNumber());
        CardExpiryDate expiryDate = CardExpiryDate.of(
                request.expiryDate().getYear(),
                request.expiryDate().getMonthValue());
        CardBalance balance = new CardBalance(BigDecimal.ZERO);

        Card card = Card.of(cardNumber, user, expiryDate, CardStatus.PENDING_ACTIVATION, balance, cardEncryption);

        return cardRepository.save(card);
    }

    @Transactional(readOnly = true)
    public final Card getCardByIdForAdmin(final Long adminId, final Long cardId) {
        validateId(adminId);
        validateId(cardId);

        userService.checkAdminPermissionTo("get card", adminId);

        return findCardByIdForAdmin(cardId);
    }

    @Transactional(readOnly = true)
    public final Page<Card> getAllCardsForAdmin(final Long adminId, final Pageable pageable) {
        validatePagination(pageable);

        validateId(adminId);
        userService.checkAdminPermissionTo("get all cards", adminId);

        return cardRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public final Card getCardByIdForOwner(final Long ownerId, final Long cardId) {
        validateId(ownerId);
        validateId(cardId);

        User owner = userService.findUserById(ownerId);
        Card card = findCardByIdForOwner(cardId, owner);

        return card;
    }

    @Transactional(readOnly = true)
    public final Page<Card> getAllCardsForOwner(final Long ownerId, final Pageable pageable) {
        if (pageable == null)
            throw new BusinessRuleViolationException("Pageable is required");

        validatePagination(pageable);

        User owner = userService.findUserById(ownerId);

        return cardRepository.findAllByOwner(owner, pageable);
    }

    @Transactional
    public final void deleteCardById(final Long adminId, final Long cardId) {
        validateId(adminId);
        validateId(cardId);

        userService.checkAdminPermissionTo("delete card", adminId);

        Card card = findCardByIdForAdmin(cardId);
        cardRepository.delete(card);
    }

    @Transactional
    public final Card activateCardById(final Long adminId, final Long cardId) {
        validateId(adminId);
        validateId(cardId);

        userService.checkAdminPermissionTo("activate card", adminId);
        Card card = findCardByIdForAdmin(cardId);

        checkExpiryDate(card);
        if (card.isActive())
            throw new BusinessRuleViolationException(String.format("Card with id=%d already ACTIVE", cardId));

        card.changeStatus(CardStatus.ACTIVE);

        return cardRepository.save(card);
    }

    @Transactional
    public final Card blockCardById(final Long adminId, final Long cardId) {
        validateId(adminId);
        validateId(cardId);

        userService.checkAdminPermissionTo("block card", adminId);
        Card card = findCardByIdForAdmin(cardId);

        checkExpiryDate(card);
        if (card.isBlocked())
            throw new BusinessRuleViolationException(String.format("Card with id=%d already BLOCKED", cardId));

        card.changeStatus(CardStatus.BLOCKED);

        return cardRepository.save(card);
    }

}
