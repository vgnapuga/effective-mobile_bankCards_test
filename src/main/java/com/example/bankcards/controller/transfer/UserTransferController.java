package com.example.bankcards.controller.transfer;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.bankcards.controller.BaseController;
import com.example.bankcards.dto.transfer.request.TransferRequest;
import com.example.bankcards.dto.transfer.response.TransferListResponse;
import com.example.bankcards.dto.transfer.response.TransferResponse;
import com.example.bankcards.model.transfer.Transfer;
import com.example.bankcards.service.TransferService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
public final class UserTransferController extends BaseController {

    private static String ROOT = "/api/transfers";

    private final TransferService transferService;

    @PostMapping
    public ResponseEntity<TransferResponse> createTransfer(
            @Valid @RequestBody final TransferRequest request,
            final Authentication authentication) {
        Long ownerId = getCurrentUserId(authentication);
        log.info("POST(id={}) - {}", ownerId, ROOT);

        Transfer transfer = transferService.transferBetweenOwnCards(ownerId, request);
        TransferResponse response = TransferResponse.of(transfer);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{transferId}")
    public ResponseEntity<TransferResponse> getTransfer(
            @PathVariable final Long transferId,
            final Authentication authentication) {
        Long ownerId = getCurrentUserId(authentication);
        log.info("GET(id={}) - {}/{}", ownerId, ROOT, transferId);

        Transfer retrievedTransfer = transferService.getTransferByIdForOwner(ownerId, transferId);
        TransferResponse response = TransferResponse.of(retrievedTransfer);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<TransferListResponse> getAllTransfers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            final Authentication authentication) {
        Long ownerId = getCurrentUserId(authentication);
        log.info("GET(id={}) - {}", ownerId, ROOT);

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Transfer> transferPage = transferService.getAllTransfersForOwner(ownerId, pageable);
        TransferListResponse response = new TransferListResponse(
                transferPage.getContent().stream().map(TransferResponse::of).toList(),
                transferPage.getTotalElements(),
                page,
                size);

        return ResponseEntity.ok(response);
    }

}
