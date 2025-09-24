package com.example.bankcards.dto.transfer.response;


import java.util.List;


public record TransferListResponse(List<TransferResponse> transfers, Long totalCount, int page, int size) {
}
