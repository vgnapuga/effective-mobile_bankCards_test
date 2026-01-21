package com.example.bankcards.controller.card;


import java.math.BigDecimal;

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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.bankcards.controller.BaseController;
import com.example.bankcards.dto.card.request.CardCreateRequest;
import com.example.bankcards.dto.card.response.CardListResponse;
import com.example.bankcards.dto.card.response.CardResponse;
import com.example.bankcards.model.card.Card;
import com.example.bankcards.model.card.CardStatus;
import com.example.bankcards.service.CardService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
@RequestMapping("/api/admin/cards")
@RequiredArgsConstructor
public final class AdminCardController extends BaseController {

    private static String ROOT = "/api/admin/cards";

    private final CardService cardService;

    @PostMapping
    public ResponseEntity<CardResponse> createCard(
            @Valid @RequestBody final CardCreateRequest request,
            final Authentication authentication) {
        Long adminId = getCurrentUserId(authentication);
        log.info("POST(id={}) - {}", adminId, ROOT);

        Card createdCard = cardService.createCard(adminId, request);
        CardResponse response = CardResponse.of(createdCard);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{cardId}")
    public ResponseEntity<CardResponse> getCard(@PathVariable final Long cardId, final Authentication authentication) {
        Long adminId = getCurrentUserId(authentication);
        log.info("GET(id={}) - {}/{}", adminId, ROOT, cardId);

        Card retrievedCard = cardService.getCardByIdForAdmin(adminId, cardId);
        CardResponse response = CardResponse.of(retrievedCard);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<CardListResponse> getAllCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(required = false) Long ownerId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) BigDecimal moreThan,
            @RequestParam(required = false) BigDecimal lessThan,
            final Authentication authentication) {
        Long adminId = getCurrentUserId(authentication);
        log.info(
                "GET:all(id={}:admin) - {} - filters: owner_id={}, status={}, more_than={}, less_than={}",
                adminId,
                ROOT,
                ownerId,
                status,
                moreThan,
                lessThan);

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        CardStatus cardStatus = status == null ? null : CardStatus.valueOf(status);

        Page<Card> cardPage = cardService.getAllCardsForAdmin(
                adminId,
                ownerId,
                cardStatus,
                moreThan,
                lessThan,
                pageable);
        CardListResponse response = new CardListResponse(
                cardPage.getContent().stream().map(CardResponse::of).toList(),
                cardPage.getTotalElements(),
                page,
                size);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<Void> deleteCard(@PathVariable final Long cardId, final Authentication authentication) {
        Long adminId = getCurrentUserId(authentication);
        log.info("DELETE(id={}) - {}/{}", adminId, ROOT, cardId);

        cardService.deleteCardById(adminId, cardId);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/activate/{cardId}")
    public ResponseEntity<CardResponse> activateCard(
            @PathVariable final Long cardId,
            final Authentication authentication) {
        Long adminId = getCurrentUserId(authentication);
        log.info("PUT(id={}) - {}/activate/{}", adminId, ROOT, cardId);

        Card activeCard = cardService.activateCardById(adminId, cardId);
        CardResponse response = CardResponse.of(activeCard);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/block/{cardId}")
    public ResponseEntity<CardResponse> blockCard(
            @PathVariable final Long cardId,
            final Authentication authentication) {
        Long adminId = getCurrentUserId(authentication);
        log.info("PUT(id={}) - {}/block/{}", adminId, ROOT, cardId);

        Card blockedCard = cardService.blockCardById(adminId, cardId);
        CardResponse response = CardResponse.of(blockedCard);

        return ResponseEntity.ok(response);
    }

}
