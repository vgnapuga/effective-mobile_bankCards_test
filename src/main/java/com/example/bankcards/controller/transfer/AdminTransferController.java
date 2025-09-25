package com.example.bankcards.controller.transfer;


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
import com.example.bankcards.dto.transfer.response.TransferListResponse;
import com.example.bankcards.dto.transfer.response.TransferResponse;
import com.example.bankcards.model.transfer.Transfer;
import com.example.bankcards.service.TransferService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/admin/transfers")
@RequiredArgsConstructor
public final class AdminTransferController extends BaseController {

    private final TransferService transferService;

    @GetMapping("/{transferId}")
    public ResponseEntity<TransferResponse> getTransfer(
            @PathVariable final Long transferId,
            final Authentication authentication) {
        final Long adminId = getCurrentUserId(authentication);

        Transfer retrievedTransfer = transferService.getTransferByIdForAdmin(adminId, transferId);
        TransferResponse response = TransferResponse.of(retrievedTransfer);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<TransferListResponse> getAllTransfers(
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "10") final int size,
            @RequestParam(defaultValue = "id") final String sortBy,
            @RequestParam(defaultValue = "asc") final String sortDirection,
            final Authentication authentication) {
        Long adminId = getCurrentUserId(authentication);
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Transfer> transferPage = transferService.getAllTransfersForAdmin(adminId, pageable);
        TransferListResponse response = new TransferListResponse(
                transferPage.getContent().stream().map(TransferResponse::of).toList(),
                transferPage.getTotalElements(),
                page,
                size);

        return ResponseEntity.ok(response);
    }

}
