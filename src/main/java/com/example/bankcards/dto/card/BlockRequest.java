package com.example.bankcards.dto.card;


import com.example.bankcards.dto.validation.ValidCardId;


public record BlockRequest(@ValidCardId Long cardId) {
}
