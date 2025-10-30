package com.example.bankcards.model.transfer;


import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.example.bankcards.exception.BusinessRuleViolationException;
import com.example.bankcards.model.BaseEntity;
import com.example.bankcards.model.card.Card;
import com.example.bankcards.model.transfer.category.TransferCategory;
import com.example.bankcards.model.transfer.converter.AmountConverter;
import com.example.bankcards.model.transfer.vo.Amount;
import com.example.bankcards.model.user.User;
import com.example.bankcards.util.constant.TransferConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "transfer_categories", joinColumns = @JoinColumn(name = "transfer_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<TransferCategory> categories = new HashSet<>();

    public Transfer() {
    }

    public static Transfer of(
            final User owner,
            final Card fromCard,
            final Card toCard,
            final Amount amount,
            final Set<TransferCategory> categories) {
        checkBusinessRules(owner, fromCard, toCard);
        return new Transfer(owner, fromCard, toCard, amount, categories);
    }

    private Transfer(
            final User owner,
            final Card fromCard,
            final Card toCard,
            final Amount amount,
            final Set<TransferCategory> categories) {
        this.owner = Objects.requireNonNull(owner, generateNullMessageFor("owner"));
        this.fromCard = Objects.requireNonNull(fromCard, generateNullMessageFor("from card"));
        this.toCard = Objects.requireNonNull(toCard, generateNullMessageFor("to card"));
        this.amount = Objects.requireNonNull(amount, generateNullMessageFor("amount"));
        this.categories = Objects.requireNonNull(categories, generateNullMessageFor("categories"));
    }

    private static final void checkBusinessRules(final User owner, final Card fromCard, final Card toCard) {
        if (!owner.equals(toCard.getOwner()) || !owner.equals(fromCard.getOwner()))
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

    public Set<TransferCategory> getCategories() {
        return Set.copyOf(this.categories);
    }

    @Override
    public String toString() {
        return String.format(
                "Transfer{owner_id=%d, from_card_id=%s, to_card_id=%s, amount=%s, categories=%s}",
                this.owner.getId(),
                this.fromCard.getId(),
                this.toCard.getId(),
                this.amount.toString(),
                this.categories.toString());
    }

}
