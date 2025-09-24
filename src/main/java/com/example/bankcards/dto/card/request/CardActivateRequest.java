package com.example.bankcards.dto.card.request;


import com.example.bankcards.dto.validation.card.ValidCardId;


public record CardActivateRequest(@ValidCardId Long cardId) {
}
