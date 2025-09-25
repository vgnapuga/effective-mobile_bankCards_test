package com.example.bankcards.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.bankcards.dto.transfer.request.TransferRequest;
import com.example.bankcards.exception.AccessDeniedException;
import com.example.bankcards.exception.BusinessRuleViolationException;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.model.card.Card;
import com.example.bankcards.model.transfer.Transfer;
import com.example.bankcards.model.transfer.vo.Amount;
import com.example.bankcards.model.user.User;
import com.example.bankcards.repository.TransferRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
@RequiredArgsConstructor
public class TransferService extends BaseService {

    private TransferRepository transferRepository;
    private UserService userService;
    private CardService cardService;

    // ---------- Helper methods ---------- //

    private Transfer findTransferByIdForAdmin(final Long transferId) {
        return transferRepository.findById(transferId).orElseThrow(
                () -> new ResourceNotFoundException(String.format("Transfer with id=%d not found", transferId)));
    }

    private final Transfer findTransferByIdForOwner(final Long transferId, final User owner) {
        Transfer transfer = transferRepository.findById(transferId).orElseThrow(
                () -> new ResourceNotFoundException(String.format("Transfer with id=%d was not found", transferId)));

        if (!transfer.getOwner().equals(owner))
            throw new AccessDeniedException(String.format("Permission to access card denied for id=%d", owner.getId()));

        return transfer;
    }

    // ------------------------------------ //

    @Transactional
    public final Transfer transferBetweenOwnCards(final Long ownerId, final TransferRequest request) {
        validateId(ownerId);

        Long fromCardId = request.fromCardId();
        Long toCardId = request.toCardId();
        Amount amount = new Amount(request.amount());

        validateId(fromCardId);
        validateId(toCardId);

        User owner = userService.findUserById(ownerId);
        Card fromCard = cardService.findCardByIdForOwner(fromCardId, owner);
        Card toCard = cardService.findCardByIdForOwner(toCardId, owner);

        if (fromCard.equals(toCard))
            throw new BusinessRuleViolationException("Cannot transfer to the same card");

        if (!fromCard.isActive() || !toCard.isActive())
            throw new BusinessRuleViolationException("Both cards for transfer must be ACTIVE");

        fromCard.subtractBalance(amount);
        toCard.addBalance(amount);

        Transfer transfer = Transfer.of(owner, fromCard, toCard, amount);

        return transferRepository.save(transfer);
    }

    @Transactional(readOnly = true)
    public final Transfer getTransferByIdForAdmin(final Long adminId, final Long transferId) {
        validateId(adminId);
        validateId(transferId);

        userService.checkAdminPermissionTo("get transfer", adminId);
        return findTransferByIdForAdmin(transferId);
    }

    @Transactional(readOnly = true)
    public final Page<Transfer> getAllTransfersForAdmin(final Long adminId, final Pageable pageable) {
        validatePagination(pageable);

        validateId(adminId);
        userService.checkAdminPermissionTo("get all transfers", adminId);

        return transferRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public final Transfer getTransferByIdForOwner(final Long ownerId, final Long transferId) {
        validateId(ownerId);
        validateId(transferId);

        User owner = userService.findUserById(ownerId);
        Transfer transfer = findTransferByIdForOwner(transferId, owner);

        return transfer;
    }

    @Transactional(readOnly = true)
    public final Page<Transfer> getAllTransfersForOwner(final Long ownerId, final Pageable pageable) {
        validatePagination(pageable);

        validateId(ownerId);
        User owner = userService.findUserById(ownerId);

        return transferRepository.findAllByOwner(owner, pageable);
    }

}
