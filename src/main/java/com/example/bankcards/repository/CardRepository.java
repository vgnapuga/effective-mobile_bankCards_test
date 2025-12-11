package com.example.bankcards.repository;


import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.bankcards.model.card.Card;
import com.example.bankcards.model.card.CardStatus;
import com.example.bankcards.model.user.User;


@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    List<Card> findAllByOwner(final User owner);

    Page<Card> findAllByOwner(final User owner, final Pageable pageable);

    @Query("SELECT c FROM Card c WHERE c.owner = :owner AND " + "(:status IS NULL OR c.status = :status) AND " +
            "(:more_than IS NULL OR c.balance >= :more_than) AND " + "(:less_than IS NULL OR c.balance <= :less_than)")
    Page<Card> findAllByOwnerWithFilters(
            @Param("owner") final User owner,
            @Param("status") CardStatus status,
            @Param("more_than") BigDecimal moreThan,
            @Param("less_than") BigDecimal lessThan,
            final Pageable pageable);

    @Query("SELECT c FROM Card c WHERE (:owner IS NULL OR c.owner.id = :owner_id) AND " +
            "(:status IS NULL OR c.status = :status) AND " + "(:more_than IS NULL OR c.balance >= :more_than) AND " +
            "(:less_than IS NULL OR c.balance <= :less_than)")
    Page<Card> findAllWithFilters(
            @Param("owner_id") Long ownerId,
            @Param("status") CardStatus status,
            @Param("more_than") BigDecimal moreThan,
            @Param("less_than") BigDecimal lessThan,
            final Pageable pageable);
}
