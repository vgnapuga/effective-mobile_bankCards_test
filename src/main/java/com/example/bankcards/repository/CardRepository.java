package com.example.bankcards.repository;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.bankcards.model.card.Card;
import com.example.bankcards.model.user.User;


@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    List<Card> findAllByOwner(final User owner);

    Page<Card> findAllByOwner(final User owner, final Pageable pageable);

}
