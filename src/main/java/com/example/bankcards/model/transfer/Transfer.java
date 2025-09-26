package com.example.bankcards.model.transfer;


import java.util.Objects;

import com.example.bankcards.exception.BusinessRuleViolationException;
import com.example.bankcards.model.BaseEntity;
import com.example.bankcards.model.card.Card;
import com.example.bankcards.model.transfer.converter.AmountConverter;
import com.example.bankcards.model.transfer.vo.Amount;
import com.example.bankcards.model.user.User;
import com.example.bankcards.util.constant.TransferConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


@Entity
@Table(name = "transfers")
public class Transfer extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false, updatable = false)
    private User owner;

    @ManyToOne()
    @JoinColumn(name = "from_card_id", nullable = false, updatable = false)
    private Card fromCard;

    @ManyToOne
    @JoinColumn(name = "to_card_id", nullable = false, updatable = false)
    private Card toCard;

    @Column(name = "amount", nullable = false, updatable = false)
    @Convert(converter = AmountConverter.class)
    private Amount amount;

    public Transfer() {
    }

    public static Transfer of(final User owner, final Card fromCard, final Card toCard, final Amount amount) {
        checkBusinessRules(owner, fromCard, toCard);
        return new Transfer(owner, fromCard, toCard, amount);
    }

    private Transfer(final User owner, final Card fromCard, final Card toCard, final Amount amount) {
        this.owner = Objects.requireNonNull(owner, generateNullMessageFor("owner"));
        this.fromCard = Objects.requireNonNull(fromCard, generateNullMessageFor("from card"));
        this.toCard = Objects.requireNonNull(toCard, generateNullMessageFor("to card"));
        this.amount = Objects.requireNonNull(amount, generateNullMessageFor("amount"));
    }

    private static final void checkBusinessRules(final User owner, final Card fromCard, final Card toCard) {
        if (!owner.getId().equals(toCard.getOwner().getId()) || !owner.getId().equals(fromCard.getOwner().getId()))
            throw new BusinessRuleViolationException(TransferConstants.Amount.BUSINESS_RULE_VIOLATED_MESSAGE);
    }

    public User getOwner() {
        return this.owner;
    }

    public Card getFromCard() {
        return this.fromCard;
    }

    public Card getToCard() {
        return this.toCard;
    }

    public Amount getAmount() {
        return this.amount;
    }

    @Override
    public String toString() {
        return String.format(
                "Transfer{owner_id=%d, from_card_id=%s, to_card_id=%s, amount=%s}",
                this.owner.getId(),
                this.fromCard.getId(),
                this.toCard.getId(),
                this.amount.toString());
    }

}
