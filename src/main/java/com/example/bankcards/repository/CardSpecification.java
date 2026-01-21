package com.example.bankcards.repository;


import java.math.BigDecimal;

import org.springframework.data.jpa.domain.Specification;

import com.example.bankcards.model.card.Card;
import com.example.bankcards.model.card.CardStatus;
import com.example.bankcards.model.user.User;


public class CardSpecification {

    public static Specification<Card> hasOwner(User owner) {
        return (root, query, cb) -> owner == null ? null : cb.equal(root.get("owner"), owner);
    }

    public static Specification<Card> hasOwnerId(Long ownerId) {
        return (root, query, cb) -> ownerId == null ? null : cb.equal(root.get("owner").get("id"), ownerId);
    }

    public static Specification<Card> hasStatus(CardStatus status) {
        return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Card> balanceGreaterThanOrEqual(BigDecimal min) {
        return (root, query, cb) -> min == null ? null : cb.greaterThanOrEqualTo(root.get("balance"), min);
    }

    public static Specification<Card> balanceLessThanOrEqual(BigDecimal max) {
        return (root, query, cb) -> max == null ? null : cb.lessThanOrEqualTo(root.get("balance"), max);
    }
}
