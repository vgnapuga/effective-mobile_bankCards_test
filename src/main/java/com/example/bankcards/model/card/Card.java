package com.example.bankcards.model.card;


import java.math.BigDecimal;
import java.util.Objects;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.example.bankcards.exception.BusinessRuleViolationException;
import com.example.bankcards.exception.DomainValidationException;
import com.example.bankcards.model.UpdatableEntity;
import com.example.bankcards.model.card.converter.CardBalanceConverter;
import com.example.bankcards.model.card.converter.CardExpiryDateConverter;
import com.example.bankcards.model.card.vo.CardBalance;
import com.example.bankcards.model.card.vo.CardExpiryDate;
import com.example.bankcards.model.card.vo.CardNumber;
import com.example.bankcards.model.transfer.vo.Amount;
import com.example.bankcards.model.user.User;
import com.example.bankcards.security.CardEncryption;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;


@Entity
@Table(name = "cards")
public class Card extends UpdatableEntity {

    @Transient
    private CardNumber cardNumber;

    @Column(name = "card_number_encrypted", nullable = false, updatable = false)
    private String encryptedCardNumber;

    @Column(name = "card_number_last4", nullable = false, updatable = false)
    private String last4;

    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id", nullable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User owner;

    @Column(name = "expiry_date", nullable = false, updatable = false)
    @Convert(converter = CardExpiryDateConverter.class)
    private CardExpiryDate expiryDate;

    @Column(name = "status", nullable = false, updatable = true)
    @Enumerated(EnumType.STRING)
    private CardStatus status;

    @Column(name = "balance", nullable = false, updatable = true)
    @Convert(converter = CardBalanceConverter.class)
    private CardBalance balance;

    public Card() {
    }

    public static Card of(
            final CardNumber cardNumber,
            final User owner,
            final CardExpiryDate expiryDate,
            final CardStatus status,
            final CardBalance balance,
            final CardEncryption cardEncryption) {
        String encryptedCardNumber = cardEncryption.encrypt(cardNumber);
        String last4 = cardNumber.getLastDigits();

        return new Card(cardNumber, encryptedCardNumber, last4, owner, expiryDate, status, balance);
    }

    private Card(
            final CardNumber cardNumber,
            final String encryptedCardNumber,
            final String last4,
            final User owner,
            final CardExpiryDate expiryDate,
            final CardStatus status,
            final CardBalance balance) {
        this.cardNumber = Objects.requireNonNull(cardNumber, generateNullMessageFor("number"));
        this.encryptedCardNumber = Objects.requireNonNull(
                encryptedCardNumber,
                generateNullMessageFor("encrypted number"));
        this.last4 = Objects.requireNonNull(last4, generateNullMessageFor("number last four digits"));
        this.owner = Objects.requireNonNull(owner, generateNullMessageFor("owner"));
        this.expiryDate = Objects.requireNonNull(expiryDate, generateNullMessageFor("expiry date"));
        this.status = Objects.requireNonNull(status, generateNullMessageFor("status"));
        this.balance = Objects.requireNonNull(balance, generateNullMessageFor("balance"));
    }

    public final void changeStatus(CardStatus newStatus) {
        if (newStatus == null)
            throw new DomainValidationException(generateNullMessageFor("new status"));

        this.status = newStatus;
    }

    public final void addBalance(final Amount amount) {
        if (amount == null)
            throw new DomainValidationException(generateNullMessageFor("amount"));

        BigDecimal newBalance = this.balance.getValue().add(amount.getValue());
        this.balance = new CardBalance(newBalance);
    }

    public final void subtractBalance(final Amount amount) {
        if (amount == null)
            throw new DomainValidationException(generateNullMessageFor("amount"));

        BigDecimal newBalance = this.balance.getValue().subtract(amount.getValue());
        if (newBalance.compareTo(BigDecimal.ZERO) < 0)
            throw new BusinessRuleViolationException("Insufficient funds");

        this.balance = new CardBalance(newBalance);
    }

    public final boolean isActive() {
        return this.status.equals(CardStatus.ACTIVE);
    }

    public final boolean isBlocked() {
        return this.status.equals(CardStatus.BLOCKED);
    }

    public final boolean isExpired() {
        return this.status.equals(CardStatus.EXPIRED);
    }

    public CardNumber getCardNumber() {
        return this.cardNumber;
    }

    public String getEncryptedCardNumber() {
        return this.encryptedCardNumber;
    }

    public String getLast4() {
        return this.last4;
    }

    public User getOwner() {
        return this.owner;
    }

    public CardExpiryDate getExpiryDate() {
        return this.expiryDate;
    }

    public CardStatus getStatus() {
        return this.status;
    }

    public CardBalance getBalance() {
        return this.balance;
    }

    @Override
    public final String toString() {
        return String.format(
                "Card{card_number=**** **** **** %s, owner=%s, expiry_date=%s, status=%s, balance=%s}",
                this.last4,
                this.owner.toString(),
                this.expiryDate.toString(),
                this.status.toString(),
                this.balance.toString());
    }

}
