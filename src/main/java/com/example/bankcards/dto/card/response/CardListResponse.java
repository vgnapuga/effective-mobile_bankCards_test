package com.example.bankcards.dto.card.response;


import java.util.List;


public record CardListResponse(List<CardResponse> cards, Long totalCount, int page, int size) {

}
