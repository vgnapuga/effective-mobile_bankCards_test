package com.example.bankcards.controller.card;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.bankcards.controller.BaseController;
import com.example.bankcards.dto.card.request.CardCreateRequest;
import com.example.bankcards.dto.card.response.CardListResponse;
import com.example.bankcards.dto.card.response.CardResponse;
import com.example.bankcards.model.card.Card;
import com.example.bankcards.service.CardService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/admin/cards")
@RequiredArgsConstructor
public final class AdminCardController extends BaseController {

    private final CardService cardService;

    @PostMapping
    public ResponseEntity<CardResponse> createCard(
            @Valid @RequestBody final CardCreateRequest request,
            final Authentication authentication) {
        Long adminId = getCurrentUserId(authentication);

        Card createdCard = cardService.createCard(adminId, request);
        CardResponse response = CardResponse.of(createdCard);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{cardId}")
    public ResponseEntity<CardResponse> getCard(@PathVariable final Long cardId, final Authentication authentication) {
        Long adminId = getCurrentUserId(authentication);

        Card retrievedCard = cardService.getCardById(adminId, cardId);
        CardResponse response = CardResponse.of(retrievedCard);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<CardListResponse> getAllCards(
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "0") final int size,
            @RequestParam(defaultValue = "id") final String sortBy,
            @RequestParam(defaultValue = "acs") final String sortDirection,
            final Authentication authentication) {
        Long adminId = getCurrentUserId(authentication);
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Card> userPage = cardService.getAllCards(adminId, pageable);
        CardListResponse response = new CardListResponse(
                userPage.getContent().stream().map(CardResponse::of).toList(),
                userPage.getTotalElements(),
                page,
                size);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<Void> deleteCard(@PathVariable final Long cardId, Authentication authentication) {
        Long adminId = getCurrentUserId(authentication);
        cardService.deleteCardById(adminId, cardId);

        return ResponseEntity.noContent().build();
    }

}
