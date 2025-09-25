package com.example.bankcards.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.bankcards.dto.transfer.request.TransferRequest;
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

        fromCard.subtractBalance(amount);
        toCard.addBalance(amount);

        Transfer transfer = Transfer.of(owner, fromCard, toCard, amount);

        return transferRepository.save(transfer);
    }

    @Transactional(readOnly = true)
    public final Page<Transfer> getAllTransfersForUser(final Long ownerId, final Pageable pageable) {
        validatePagination(pageable);

        validateId(ownerId);
        User owner = userService.findUserById(ownerId);

        return transferRepository.findAllByOwner(owner, pageable);
    }

}
