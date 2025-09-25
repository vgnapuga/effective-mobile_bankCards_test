package com.example.bankcards.controller.card;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.bankcards.controller.BaseController;
import com.example.bankcards.dto.card.response.CardListResponse;
import com.example.bankcards.dto.card.response.CardResponse;
import com.example.bankcards.model.card.Card;
import com.example.bankcards.service.CardService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public final class UserCardController extends BaseController {

    private final CardService cardService;

    @GetMapping("/{cardId}")
    public ResponseEntity<CardResponse> getCard(@PathVariable final Long cardId, final Authentication authentication) {
        Long ownerId = getCurrentUserId(authentication);

        Card retrievedCard = cardService.getCardByIdForOwner(ownerId, cardId);
        CardResponse response = CardResponse.of(retrievedCard);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<CardListResponse> getAllCards(
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "10") final int size,
            @RequestParam(defaultValue = "id") final String sortBy,
            @RequestParam(defaultValue = "acs") final String sortDirection,
            final Authentication authentication) {
        Long ownerId = getCurrentUserId(authentication);
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Card> userPage = cardService.getAllCardsForOwner(ownerId, pageable);
        CardListResponse response = new CardListResponse(
                userPage.getContent().stream().map(CardResponse::of).toList(),
                userPage.getTotalElements(),
                page,
                size);

        return ResponseEntity.ok(response);
    }

}
