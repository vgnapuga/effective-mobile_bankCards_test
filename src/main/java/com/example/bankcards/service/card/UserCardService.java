package com.example.bankcards.service.card;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.bankcards.dto.card.request.CardGetRequest;
import com.example.bankcards.exception.AccessDeniedException;
import com.example.bankcards.exception.BusinessRuleViolationException;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.model.card.Card;
import com.example.bankcards.model.user.User;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.service.BaseService;
import com.example.bankcards.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserCardService extends BaseService {

    private final CardRepository cardRepository;
    private final UserService userService;

    // ---------- Helper methods ---------- //

    private final Card findCardByIdForOwner(final Long cardId, final User owner) {
        Card card = cardRepository.findById(cardId).orElseThrow(
                () -> new ResourceNotFoundException(String.format("Card with id=%d was not found", cardId)));

        if (!card.getOwner().equals(owner))
            throw new AccessDeniedException(String.format("Permission to access card denied for id=%d", owner.getId()));

        return card;
    }

    // ------------------------------------ //

    @Transactional(readOnly = true)
    public final Card getCardById(final Long ownerId, final CardGetRequest request) {
        Long cardId = request.cardId();

        validateId(ownerId);
        validateId(cardId);

        User owner = userService.findUserById(ownerId);
        Card card = findCardByIdForOwner(cardId, owner);

        return card;
    }

    @Transactional(readOnly = true)
    public final Page<Card> getAllCards(final Long ownerId, final Pageable pageable) {
        if (pageable == null)
            throw new BusinessRuleViolationException("Pageable is required");

        validatePagination(pageable);

        User owner = userService.findUserById(ownerId);

        return cardRepository.findAllByOwner(owner, pageable);
    }

}
